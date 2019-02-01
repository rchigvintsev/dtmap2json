package org.briarheart.doomthree.map.entity;

import java.util.Map;

/**
 * @author Roman Chigvintsev
 */
public abstract class AbstractModel extends Entity {
    public AbstractModel(String modelBody) {
        super(modelBody);
    }

    public abstract String getMesh();

    public abstract Map<String, String> getAnimations();
}
