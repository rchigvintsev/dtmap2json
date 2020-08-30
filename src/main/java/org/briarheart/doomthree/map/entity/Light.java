package org.briarheart.doomthree.map.entity;

import org.apache.commons.lang3.StringUtils;
import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.util.Vector3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Chigvintsev
 */
public class Light extends Entity {
    private static final Pattern COLOR_PATTERN = Pattern.compile("\"_color\"\\s+\"([0-9 .]+)\"");
    private static final Pattern LIGHT_RADIUS_PATTERN = Pattern.compile("\"light_radius\"\\s+\"([0-9 .]+)\"");

    private static final double DISTANCE_FACTOR = 2.0d;

    private String type = "point";
    private Vector3 position;
    private String color;
    private int distance;
    private boolean castShadow;

    public Light(String entityBody) {
        super(entityBody);
    }

    @Override
    public boolean visit(AbstractMap map, boolean lastAttempt) {
        Area targetArea = null;
        for (Area area : map.getAreas()) {
            if (area.containsPoint(position)) {
                targetArea = area;
                if (map.matchesAreaFilter(area.getName())) {
                    break;
                }
            }
        }

        if (targetArea != null) {
            targetArea.addLight(this);
            return true;
        }

        if (StringUtils.isEmpty(map.getAreaFilter())) {
            map.addLight(this);
            return true;
        }

        return false;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public boolean isCastShadow() {
        return castShadow;
    }

    public void setCastShadow(boolean castShadow) {
        this.castShadow = castShadow;
    }

    @Override
    public String toJson() {
        String json = "{\"name\":\"" + getName() + "\",\"type\":\"" + type + "\",\"position\":" + position;
        if (color != null) {
            json += ",\"color\":\"" + color + "\"";
        }
        return json + ",\"distance\":" + distance + ",\"castShadow\":" + castShadow + "}";
    }

    @Override
    protected void parse(String body) {
        super.parse(body);
        position = parseOrigin(body);
        color = parseColor(body);
        Vector3 lightRadius = parseLightRadius(body);
        distance = (int) (getMaxRadius(lightRadius) * DISTANCE_FACTOR);
    }

    protected String parseColor(String s) {
        Matcher matcher = COLOR_PATTERN.matcher(s);
        if (matcher.find()) {
            StringBuilder color = new StringBuilder("#");
            String[] rgb = matcher.group(1).split(" ");
            for (String component : rgb) {
                component = Integer.toString(Math.round(Float.parseFloat(component) * 255), 16);
                if (component.length() < 2)
                    color.append('0');
                color.append(component);
            }
            return color.toString();
        }
        System.err.println("Failed to parse light color");
        return null;
    }

    protected Vector3 parseLightRadius(String s) {
        Matcher matcher = LIGHT_RADIUS_PATTERN.matcher(s);
        if (matcher.find())
            return Vector3.fromString(matcher.group(1));
        System.err.println("Failed to parse light radius");
        return new Vector3();
    }

    private double getMaxRadius(Vector3 lightRadius) {
        double max = Double.compare(lightRadius.x, lightRadius.y) > 0 ? lightRadius.x : lightRadius.y;
        return Double.compare(max, lightRadius.z) > 0 ? lightRadius.x : lightRadius.y;
    }
}
