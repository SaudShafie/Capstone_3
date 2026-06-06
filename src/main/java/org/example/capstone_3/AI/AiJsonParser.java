package org.example.capstone_3.AI;

import tools.jackson.databind.JsonNode;
import tools.jackson.databind.json.JsonMapper;

/**
 * Parses JSON strings returned by {@link AiService#ask(String)}.
 */
public final class AiJsonParser {

    private static final JsonMapper JSON = JsonMapper.builder().build();

    private AiJsonParser() {
    }

    public static JsonNode parseObject(String json) {
        try {
            JsonNode node = JSON.readTree(json);
            if (node == null || !node.isObject()) {
                throw new AiException("AI response is not a JSON object.");
            }
            return node;
        } catch (AiException ex) {
            throw ex;
        } catch (Exception ex) {
            throw new AiException("AI response is not valid JSON: " + ex.getMessage(), ex);
        }
    }

    public static boolean requireBoolean(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || !value.isBoolean()) {
            throw new AiException("AI response did not contain " + field + ".");
        }
        return value.asBoolean();
    }

    public static int requireInt(JsonNode node, String field, int min, int max) {
        JsonNode value = node.get(field);
        if (value == null || !value.isNumber()) {
            throw new AiException("AI response did not contain " + field + ".");
        }
        int number = value.asInt();
        if (number < min || number > max) {
            throw new AiException("AI " + field + " must be between " + min + " and " + max + ".");
        }
        return number;
    }

    public static String requireText(JsonNode node, String field) {
        JsonNode value = node.get(field);
        if (value == null || value.asString().isBlank()) {
            throw new AiException("AI response did not contain " + field + ".");
        }
        return value.asString().trim();
    }
}
