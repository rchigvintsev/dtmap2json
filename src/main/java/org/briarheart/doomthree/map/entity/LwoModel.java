package org.briarheart.doomthree.map.entity;

import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.util.Matrix4;
import org.briarheart.doomthree.util.Vector3;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Chigvintsev
 */
public class LwoModel extends AbstractModel {
    private static final Pattern NAME_PATTERN = Pattern.compile("\"name\"\\s+\"(\\w+)\"");
    private static final Pattern MODEL_PATTERN = Pattern.compile("\"model\"\\s+\"([\\w/.]+)\"");
    private static final Pattern ROTATION_PATTERN = Pattern.compile("\"rotation\"\\s+\"([0-9 -.e]+)\"");
    private static final Pattern SKIN_PATTERN = Pattern.compile("\"skin\"\\s+\"([\\w/]+)\"");

    private String name;
    private String mesh;
    private Vector3 position;
    private Vector3 rotation;
    private String skin;

    public LwoModel(String modelBody) {
        super(modelBody);
    }

    public static boolean isLwoModel(String modelName) {
        return modelName.toLowerCase().endsWith(".lwo");
    }

    @Override
    public boolean visit(AbstractMap map, boolean warnIfFailed) {
        for (Area area : map.getAreas())
            if (area.getBoundingBox().contains(position)) {
                area.addModel(this);
                return true;
            }

        if (warnIfFailed)
            System.err.println("Could not find area to accommodate LWO model with name \"" + name + "\"");
        return false;
    }

    @Override
    public String getMesh() {
        return mesh;
    }

    @Override
    public Map<String, String> getAnimations() {
        return Collections.emptyMap();
    }

    @Override
    public String toJson() {
        return "{"
                + "\"name\":\"" + mesh + "\","
                + "\"position\":" + position + ","
                + "\"rotation\":" + rotation
                + (skin == null ? "" : ",\"skin\":\"" + skin + "\"")
                + "}";
    }

    @Override
    protected void parse(String body) {
        Matcher nameMatcher = NAME_PATTERN.matcher(body);
        if (nameMatcher.find())
            name = nameMatcher.group(1);
        else
            System.err.println("Failed to parse LWO model name");
        Matcher modelMatcher = MODEL_PATTERN.matcher(body);
        if (modelMatcher.find())
            mesh = modelMatcher.group(1);
        position = parseOrigin(body);
        rotation = parseRotation(body);
        skin = parseSkin(body);
    }

    private Vector3 parseRotation(String s) {
        Matcher matcher = ROTATION_PATTERN.matcher(s);
        if (matcher.find()) {
            Matrix4 rotationMatrix = Matrix4.fromString(matcher.group(1));
            return rotationMatrix.toAngles();
        }
        return new Vector3();
    }

    private String parseSkin(String s) {
        Matcher matcher = SKIN_PATTERN.matcher(s);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }
}
