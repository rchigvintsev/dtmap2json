package org.briarheart.doomthree.entity;

import org.apache.commons.lang3.StringUtils;
import org.briarheart.doomthree.AbstractMap;
import org.briarheart.doomthree.model.Model;
import org.briarheart.doomthree.util.Vector3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Chigvintsev
 */
public class Light extends Entity {
    private static final Pattern COLOR_PATTERN = Pattern.compile("\"_color\"\\s+\"([0-9 .]+)\"");

    private String type = "point";
    private Vector3 position;
    private String color;
    private int distance = 272;

    public Light(String entityBody) {
        super(entityBody);
    }

    @Override
    public void visit(AbstractMap map) {
        for (Model model : map.getModels())
            if (model.getBoundingBox().contains(position)) {
                model.getLights().add(this);
                return;
            }
        if (StringUtils.isEmpty(map.getAreaFilter()))
            map.getLights().add(this);
    }

    @Override
    public String toJson() {
        String json = "{\"type\":\"" + type + "\",\"position\":" + position;
        if (color != null)
            json += ",\"color\":\"" + color + "\"";
        return json + ",\"distance\":" + distance + "}";
    }

    @Override
    protected void parse(String body) {
        position = parseOrigin(body);
        Matcher matcher = COLOR_PATTERN.matcher(body);
        if (matcher.find()) {
            StringBuilder color = new StringBuilder("#");
            String[] rgb = matcher.group(1).split(" ");
            for (String component : rgb) {
                component = Integer.toString(Math.round(Float.parseFloat(component) * 255), 16);
                if (component.length() < 2)
                    color.append('0');
                color.append(component);
            }
            this.color = color.toString();
        } else
            System.err.println("Failed to parse light color");
    }
}
