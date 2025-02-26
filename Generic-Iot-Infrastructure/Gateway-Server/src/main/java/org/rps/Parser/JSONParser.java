package org.rps.Parser;


import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

public class JSONParser implements Parser<Map<String, String>, JsonObject>{

    @Override
    public Map<String, String> parse(JsonObject json) {
        Map<String, String> resultMap = new HashMap<>();

        if (json == null || !json.has("command") || !json.has("args")) {
            return null; // Return null if format is incorrect
        }

        for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
            String key = entry.getKey();
            JsonElement valueElement = entry.getValue();

            if (valueElement.isJsonObject()) {
                JsonObject nestedObject = valueElement.getAsJsonObject();
                for (Map.Entry<String, JsonElement> nestedEntry : nestedObject.entrySet()) {
                    resultMap.put(nestedEntry.getKey(), nestedEntry.getValue().getAsString());
                }
            } else {
                resultMap.put(key, valueElement.getAsString());
            }
        }

        return resultMap;

    }
}
