package org.briarheart.doomthree.map.game;

import com.google.gson.*;
import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.map.area.surface.Surface;
import org.briarheart.doomthree.map.entity.InfoPlayerStart;
import org.briarheart.doomthree.map.entity.Light;
import org.briarheart.doomthree.map.util.Entities;
import org.briarheart.doomthree.util.Angles;
import org.briarheart.doomthree.util.BoundingBox;
import org.briarheart.doomthree.util.Vector3;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Roman Chigvintsev
 */
public class Site3 extends AbstractMap {
    public static final String NAME = "game/site3";

    private final Map<String, Map<String, Object>> overriddenLightProperties;
    private final Map<String, List<BoundingBox>> boundingBoxes;

    public Site3(String areaFilter) {
        super(NAME, areaFilter);
        this.overriddenLightProperties = loadOverriddenLightProperties();
        this.boundingBoxes = loadBoundingBoxes();
    }

    @Override
    public Surface newSurface(Area area, String surfaceBody) {
        return new Site3Surface(area, surfaceBody);
    }

    @Override
    public Area newArea(String body) {
        Site3Area area = new Site3Area(this, body, overriddenLightProperties);
        List<BoundingBox> boundingBoxes = this.boundingBoxes.get(area.getName());
        if (boundingBoxes != null) {
            area.setBoundingBoxes(boundingBoxes);
        }
        return area;
    }

    @Override
    public void setInfoPlayerStart(InfoPlayerStart infoPlayerStart) {
        super.setInfoPlayerStart(infoPlayerStart);
        infoPlayerStart.setRotation(new Vector3(0, Angles.degreesToRadians(180), 0));
    }

    @Override
    public void addLight(Light light) {
        Entities.overrideEntityProperties(light, overriddenLightProperties.get(light.getName()));
        super.addLight(light);
    }

    private Map<String, Map<String, Object>> loadOverriddenLightProperties() {
        Map<String, Map<String, Object>> result = new HashMap<>();
        ClassLoader classLoader = getClass().getClassLoader();
        URL lightUrl = classLoader.getResource("map/game/site3/lights.json");
        if (lightUrl != null) {
            try {
                String json = new String(Files.readAllBytes(Paths.get(lightUrl.toURI())));
                JsonArray lights = JsonParser.parseString(json).getAsJsonArray();
                for (JsonElement lightElement : lights) {
                    JsonObject lightObject = lightElement.getAsJsonObject();
                    String name = lightObject.get("name").getAsString();
                    Map<String, Object> lightProperties = new HashMap<>();
                    for (Map.Entry<String, JsonElement> entry : lightObject.entrySet()) {
                        JsonPrimitive primitive = entry.getValue().getAsJsonPrimitive();
                        if (primitive.isBoolean()) {
                            lightProperties.put(entry.getKey(), primitive.getAsBoolean());
                        } else if (primitive.isNumber()) {
                            lightProperties.put(entry.getKey(), primitive.getAsNumber());
                        } else if (primitive.isString()) {
                            lightProperties.put(entry.getKey(), primitive.getAsString());
                        }
                    }
                    result.put(name, lightProperties);
                }
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException("Failed to load overridden light definitions", e);
            }
        }
        return result;
    }

    private Map<String, List<BoundingBox>> loadBoundingBoxes() {
        Map<String, List<BoundingBox>> result = new HashMap<>();
        ClassLoader classLoader = getClass().getClassLoader();
        URL boundingBoxesUrl = classLoader.getResource("map/game/site3/bounding-boxes.json");
        if (boundingBoxesUrl != null) {
            try {
                String json = new String(Files.readAllBytes(Paths.get(boundingBoxesUrl.toURI())));
                JsonObject boundingBoxes = JsonParser.parseString(json).getAsJsonObject();
                for (Map.Entry<String, JsonElement> bbElement : boundingBoxes.entrySet()) {
                    result.put(bbElement.getKey(), new ArrayList<>());
                    JsonArray bbArray = bbElement.getValue().getAsJsonArray();
                    for (JsonElement bbValueElement : bbArray) {
                        JsonArray bbValueArray = bbValueElement.getAsJsonArray();

                        double minX = bbValueArray.get(0).getAsDouble();
                        double maxX = bbValueArray.get(1).getAsDouble();

                        double minY = bbValueArray.get(2).getAsDouble();
                        double maxY = bbValueArray.get(3).getAsDouble();

                        double minZ = bbValueArray.get(4).getAsDouble();
                        double maxZ = bbValueArray.get(5).getAsDouble();

                        result.get(bbElement.getKey()).add(new BoundingBox(minX, maxX, minY, maxY, minZ, maxZ));
                    }
                }
            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException("Failed to load bounding box definitions", e);
            }
        }
        return result;
    }
}
