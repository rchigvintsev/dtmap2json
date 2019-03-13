package org.briarheart.doomthree.map.entity.model;

import org.briarheart.doomthree.map.entity.Entity;

import java.util.Collections;
import java.util.Map;

/**
 * @author Roman Chigvintsev
 */
public abstract class AbstractModel extends Entity {
    public AbstractModel(String modelBody) {
        super(modelBody);
    }

    public abstract String getModel();

    public abstract Map<String, String> getAnimations();

    public Map<String, String> getSounds() {
        return Collections.emptyMap();
    }
}
