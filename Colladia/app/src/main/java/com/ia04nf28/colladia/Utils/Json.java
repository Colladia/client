package com.ia04nf28.colladia.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * Created by Mar on 28/05/2016.
 */
public class Json {
    // serialise a Map<String, String> into a json string
    public static String serializeStringMap(Map<String, String> map) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = null;

        try {
            jsonString = mapper.writeValueAsString(map);
        }
        catch (JsonProcessingException e) {
            // DEBUG
            e.printStackTrace();

            throw new RuntimeException(e.getMessage());
        }
        return jsonString;
    }

    // deserialize a json string into a Map<String, String>
    public static Map<String, String> deserializeStringMap (String serialized) {
        ObjectMapper mapper = new ObjectMapper();
        Map<String, String> map = null;

        try {
            map = mapper.readValue(serialized, new TypeReference<Map<String, Object>>(){});
        }
        catch (IOException e) {
            // DEBUG
            e.printStackTrace();

            throw new RuntimeException(e.getMessage());
        }
        return map;
    }

    // serialize a List<String> into a json string
    public static String serializeStringList(List<String> array) {
        ObjectMapper mapper = new ObjectMapper();
        String jsonString = null;

        try {
            jsonString = mapper.writeValueAsString(array);
        }
        catch (JsonProcessingException e) {
            // DEBUG
            e.printStackTrace();

            throw new RuntimeException(e.getMessage());
        }
        return jsonString;
    }

    // deserialize a json string into a List<String>
    public static List<String> deserializeStringList (String serialized) {
        ObjectMapper mapper = new ObjectMapper();
        List<String> array = null;

        try {
            array = mapper.readValue(serialized, new TypeReference<List<String>>(){});
        }
        catch (IOException e) {
            // DEBUG
            e.printStackTrace();

            throw new RuntimeException(e.getMessage());
        }
        return array;
    }

    public static boolean isJSONObject(String str) {
        return str.startsWith("{");
    }

}
