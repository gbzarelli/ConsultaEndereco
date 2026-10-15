package br.com.helpdev.consultaendereco.consultas.cep;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

final class CepResponseParser {
    private CepResponseParser() { }

    static String normalize(String cep) {
        if (cep == null) throw new IllegalArgumentException("CEP ausente");
        String normalized = cep.replaceAll("\\D", "");
        if (normalized.length() != 8) throw new IllegalArgumentException("CEP deve conter oito digitos");
        return normalized;
    }

    static String fromViaCep(String json, String normalizedCep) {
        JsonObject object = parseObject(json);
        if (object == null || (object.has("erro") && object.get("erro").getAsBoolean())) return null;
        return format(value(object, "logradouro"), value(object, "bairro"),
                value(object, "localidade"), value(object, "uf"),
                value(object, "cep"), normalizedCep);
    }

    static String fromBrasilApi(String json, String normalizedCep) {
        JsonObject object = parseObject(json);
        if (object == null) return null;
        return format(value(object, "street"), value(object, "neighborhood"),
                value(object, "city"), value(object, "state"),
                value(object, "cep"), normalizedCep);
    }

    private static JsonObject parseObject(String json) {
        try {
            if (json == null) return null;
            return JsonParser.parseString(json).getAsJsonObject();
        } catch (RuntimeException ignored) {
            return null;
        }
    }

    private static String value(JsonObject object, String name) {
        if (!object.has(name) || object.get(name).isJsonNull()) return "";
        return object.get(name).getAsString().trim();
    }

    private static String format(String street, String neighborhood, String city,
            String state, String responseCep, String requestedCep) {
        if (city.isEmpty() || state.isEmpty()) return null;
        String cep = responseCep.isEmpty() ? requestedCep : responseCep;
        StringBuilder address = new StringBuilder();
        append(address, street);
        append(address, neighborhood);
        String locality = city + " - " + state;
        append(address, locality);
        append(address, cep);
        append(address, "Brasil");
        return address.toString();
    }

    private static void append(StringBuilder result, String part) {
        if (!part.isEmpty()) {
            if (result.length() > 0) result.append(", ");
            result.append(part);
        }
    }
}
