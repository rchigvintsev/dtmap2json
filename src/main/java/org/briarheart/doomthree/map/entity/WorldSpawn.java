package org.briarheart.doomthree.map.entity;

import org.briarheart.doomthree.map.AbstractMap;

/**
 * @author Roman Chigvintsev
 */
public class WorldSpawn extends Entity {
    public WorldSpawn(String entityBody) {
        super(entityBody);
    }

    @Override
    public boolean visit(AbstractMap map, boolean lastAttempt) {
        return true;
    }

    @Override
    public String toJson() {
        return "";
    }

    @Override
    protected void parse(String body) {

    }
}
