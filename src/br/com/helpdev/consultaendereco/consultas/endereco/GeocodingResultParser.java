package br.com.helpdev.consultaendereco.consultas.endereco;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public final class GeocodingResultParser {
    private GeocodingResultParser() { }

    public static String formatAddressLines(List<String> lines) {
        StringBuilder result = new StringBuilder();
        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) continue;
            if (result.length() > 0) result.append(", ");
            result.append(line.trim());
        }
        return result.toString();
    }

    public static List<String> parseNominatim(String json) throws Exception {
        JSONArray response = new JSONArray(json);
        List<String> results = new ArrayList<>();
        for (int i = 0; i < response.length(); i++) {
            JSONObject item = response.optJSONObject(i);
            if (item == null) continue;
            String displayName = item.optString("display_name", "").trim();
            if (!displayName.isEmpty() && !results.contains(displayName)) {
                results.add(displayName);
            }
        }
        return results;
    }
}
