package org.briarheart.doomthree.map.entity.model;

import org.apache.commons.lang3.StringUtils;
import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.map.area.surface.Surface;
import org.briarheart.doomthree.map.entity.Entity;
import org.briarheart.doomthree.util.Vector3;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * @author Roman Chigvintsev
 */
public abstract class AbstractModel extends Entity {
    private final List<Surface> boundSurfaces = new ArrayList<>();

    private Vector3 position;

    public AbstractModel(String modelBody) {
        super(modelBody);
    }

    public abstract String getModel();

    public abstract Map<String, String> getAnimations();

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public Map<String, String> getSounds() {
        return Collections.emptyMap();
    }

    public List<Surface> getBoundSurfaces() {
        return boundSurfaces;
    }

    protected void appendBoundSurfaces(StringBuilder json) {
        if (!boundSurfaces.isEmpty()) {
            json.append(",\"surfaces\":[");
            for (int i = 0; i < boundSurfaces.size(); i++) {
                Surface surface = boundSurfaces.get(i);
                if (i > 0)
                    json.append(",");
                json.append(surface);
            }
            json.append("]");
        }
    }

    protected Area findTargetArea(AbstractMap map) {
        Area targetArea = null;
        for (Area area : map.getAreas()) {
            if (area.getBoundingBox().contains(getPosition())) {
                targetArea = area;
                if (StringUtils.isEmpty(map.getAreaFilter()) || map.getAreaFilter().equals(area.getName())) {
                    break;
                }
            }
        }
        return targetArea;
    }
}
