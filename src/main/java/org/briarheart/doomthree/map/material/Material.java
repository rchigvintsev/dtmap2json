package org.briarheart.doomthree.map.material;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roman Chigvintsev
 */
public class Material {
    private final String name;
    private final Map<String, String> parameters = new HashMap<>();

    public Material(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String toJson() {
        if (parameters.isEmpty()) {
            return "\"" + name + "\"";
        }
        StringBuilder json = new StringBuilder("{\"name\":\"");
        json.append(name).append("\",\"parameters\":{");
        boolean firstParam = true;
        for (Map.Entry<String, String> entry : parameters.entrySet()) {
            if (!firstParam) {
                json.append(",");
            }
            json.append("\"").append(entry.getKey()).append("\":\"").append(entry.getValue()).append("\"");
            firstParam = false;
        }
        json.append("}}");
        return json.toString();
    }
}
