package org.briarheart.doomthree.map.entity.model;

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
}
