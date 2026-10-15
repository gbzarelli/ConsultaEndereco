package br.com.helpdev.consultaendereco.consultas.cep;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class ConsultaCEP {
    private static final int CONNECT_TIMEOUT_MS = 5000;
    private static final int READ_TIMEOUT_MS = 7000;
    private final ApiClient apiClient;

    public interface ApiClient {
        String viaCep(String cep) throws IOException;
        String brasilApi(String cep) throws IOException;
    }

    public ConsultaCEP() {
        this(new HttpApiClient());
    }

    ConsultaCEP(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public String getEndereco(String cep) throws Exception {
        String normalizedCep = CepResponseParser.normalize(cep);
        IOException providerFailure = null;
        try {
            String result = CepResponseParser.fromViaCep(apiClient.viaCep(normalizedCep), normalizedCep);
            if (result != null) return result;
        } catch (IOException exception) {
            providerFailure = exception;
        }
        try {
            String result = CepResponseParser.fromBrasilApi(apiClient.brasilApi(normalizedCep), normalizedCep);
            if (result != null) return result;
        } catch (IOException exception) {
            if (providerFailure != null) exception.addSuppressed(providerFailure);
            throw exception;
        }
        if (providerFailure != null) throw providerFailure;
        return "";
    }

    private static final class HttpApiClient implements ApiClient {
        @Override
        public String viaCep(String cep) throws IOException {
            return request("https://viacep.com.br/ws/" + cep + "/json/");
        }

        @Override
        public String brasilApi(String cep) throws IOException {
            return request("https://brasilapi.com.br/api/cep/v2/" + cep);
        }

        private String request(String endpoint) throws IOException {
            HttpURLConnection connection = (HttpURLConnection) new URL(endpoint).openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(CONNECT_TIMEOUT_MS);
            connection.setReadTimeout(READ_TIMEOUT_MS);
            connection.setRequestProperty("Accept", "application/json");
            try {
                int status = connection.getResponseCode();
                if (status == HttpURLConnection.HTTP_NOT_FOUND) return "{}";
                InputStream stream = status >= 200 && status < 300
                        ? connection.getInputStream() : connection.getErrorStream();
                if (status < 200 || status >= 300) {
                    throw new IOException("CEP provider returned HTTP " + status);
                }
                if (stream == null) throw new IOException("CEP provider returned no response body");
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                    StringBuilder body = new StringBuilder();
                    String line;
                    while ((line = reader.readLine()) != null) body.append(line);
                    return body.toString();
                }
            } finally {
                connection.disconnect();
            }
        }
    }
}
