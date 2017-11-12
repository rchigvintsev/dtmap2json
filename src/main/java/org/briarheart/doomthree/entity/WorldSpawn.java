package org.briarheart.doomthree.entity;

import org.briarheart.doomthree.Map;

/**
 * @author Roman Chigvintsev
 */
public class WorldSpawn extends Entity {
    public WorldSpawn(String entityBody) {
        super(entityBody);
    }

    @Override
    public void visit(Map map) {
    }

    @Override
    public String toJson() {
        return "";
    }

    @Override
    protected void parse(String body) {

    }
}
