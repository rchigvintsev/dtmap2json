package org.briarheart.doomthree.map.entity;

import org.briarheart.doomthree.map.AbstractMap;

/**
 * @author Roman Chigvintsev
 */
public class Skybox extends Entity {
    private static final double DEFAULT_SIZE = 100_000.0;

    private final String materialName;
    private final double size;

    public Skybox(String materialName) {
        super(null);
        this.materialName = materialName;
        this.size = DEFAULT_SIZE;
    }

    @Override
    public boolean visit(AbstractMap map, boolean lastAttempt) {
        map.setSkybox(this);
        return true;
    }

    @Override
    public String toJson() {
        return "\"skybox\":{\"material\":" + "\"" + materialName + "\",\"size\":" + size + "}";
    }

    @Override
    protected void parse(String body) {
        // Do nothing
    }

    public void updateMapMeta(AbstractMap.Meta meta) {
        meta.getMaterials().add(materialName);
    }
}
