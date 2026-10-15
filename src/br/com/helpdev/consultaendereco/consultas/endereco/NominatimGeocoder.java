package br.com.helpdev.consultaendereco.consultas.endereco;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/** Fallback geocoder for devices whose platform Geocoder has no results. */
public final class NominatimGeocoder {
    private static final String ENDPOINT = "https://nominatim.openstreetmap.org/search?";
    private static final String USER_AGENT = "ConsultaEndereco/3.0 (https://helpdev.com.br; contato: gbzarelli@helpdev.com.br)";
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 7000;
    private static final long MIN_REQUEST_INTERVAL_MS = 1000;
    private static final int MAX_CACHE_ENTRIES = 50;
    private static long lastRequestStartedAt;
    private static final Map<String, List<String>> CACHE = new LinkedHashMap<String, List<String>>(16, 0.75f, true) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<String, List<String>> eldest) {
            return size() > MAX_CACHE_ENTRIES;
        }
    };

    private NominatimGeocoder() { }

    public static List<String> search(String query) throws Exception {
        String cacheKey = query.trim().toLowerCase(Locale.ROOT);
        synchronized (CACHE) {
            List<String> cached = CACHE.get(cacheKey);
            if (cached != null) return new ArrayList<>(cached);
        }

        waitForRateLimit();
        HttpURLConnection connection = (HttpURLConnection) new URL(ENDPOINT
                + "q=" + URLEncoder.encode(query, "UTF-8")
                + "&format=jsonv2&limit=20&accept-language=pt-BR").openConnection();
        connection.setRequestMethod("GET");
        connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
        connection.setReadTimeout(READ_TIMEOUT_MS);
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setRequestProperty("Accept", "application/json");
        try {
            int status = connection.getResponseCode();
            if (status < 200 || status >= 300) {
                throw new IOException("Nominatim returned HTTP " + status);
            }
            InputStream stream = connection.getInputStream();
            if (stream == null) throw new IOException("Nominatim returned no response body");
            StringBuilder body = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) body.append(line);
            }
            List<String> results = GeocodingResultParser.parseNominatim(body.toString());
            synchronized (CACHE) {
                CACHE.put(cacheKey, new ArrayList<>(results));
            }
            return results;
        } finally {
            connection.disconnect();
        }
    }

    private static void waitForRateLimit() throws InterruptedException {
        synchronized (NominatimGeocoder.class) {
            long waitMs = MIN_REQUEST_INTERVAL_MS - (System.currentTimeMillis() - lastRequestStartedAt);
            if (waitMs > 0) Thread.sleep(waitMs);
            lastRequestStartedAt = System.currentTimeMillis();
        }
    }
}
