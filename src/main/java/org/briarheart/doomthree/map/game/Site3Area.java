package org.briarheart.doomthree.map.game;

import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.map.entity.Light;
import org.briarheart.doomthree.map.util.Entities;

import java.util.Map;

/**
 * @author Roman Chigvintsev
 */
public class Site3Area extends Area {
    private final Map<String, Map<String, Object>> overriddenLightProperties;

    public Site3Area(AbstractMap map, String areaBody, Map<String, Map<String, Object>> overriddenLightProperties) {
        super(map, areaBody);
        this.overriddenLightProperties = overriddenLightProperties;
    }

    @Override
    public void addLight(Light light) {
        Entities.overrideEntityProperties(light, overriddenLightProperties.get(light.getName()));
        super.addLight(light);
    }
}
