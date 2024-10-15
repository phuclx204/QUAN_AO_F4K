package org.example.quan_ao_f4k.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.util.List;

public class JacksonEx {

    public static final ObjectMapper INIT_MAPPER = new ObjectMapper();

    public static <T> T convertToType(Object obj, Class<T> valueType) {
        try {
            JsonNode jsonNode = INIT_MAPPER.valueToTree(obj);
            return INIT_MAPPER.treeToValue(jsonNode, valueType);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static <T> JsonNode convertObject2JsonNode(T obj) {
        return convertToType(obj, JsonNode.class);
    }

    public static <T> ObjectNode convertObject2Node(T obj) {
        ObjectMapper mapper = new ObjectMapper();
        return mapper.valueToTree(obj);
    }

    public static <T> ArrayNode convertList2ArrayNode(List<T> list) {
        ArrayNode arrayNode = INIT_MAPPER.createArrayNode();

        for (T obj : list) {
            arrayNode.add(INIT_MAPPER.valueToTree(obj));
        }
        return arrayNode;
    }

    public static <T> T getDataFromJsonNode(JsonNode jsonNode, String fieldName, Class<T> type) {
        JsonNode fieldNode = jsonNode.get(fieldName);
        if (fieldNode == null) {
            return null;
        }

        return convertJsonNode2Type(fieldNode, type);
    }

    public static <T> T convertJsonNode2Type(JsonNode jsonNode, Class<T> type) {
        try {
            return INIT_MAPPER.treeToValue(jsonNode, type);
        } catch (Exception e) {
            throw new RuntimeException("Failed to convert JsonNode to " + type.getSimpleName(), e);
        }
    }

    private static <T> T getDefaultValue(Class<T> type) {
        if (type == String.class) return (T) "";
        if (type == Integer.class) return (T) Integer.valueOf(0);
        if (type == Boolean.class) return (T) Boolean.FALSE;
        if (type == Double.class) return (T) Double.valueOf(0.0);
        if (type == Long.class) return (T) Long.valueOf(0L);
        return null;
    }
}
