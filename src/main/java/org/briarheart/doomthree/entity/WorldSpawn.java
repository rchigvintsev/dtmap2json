package org.briarheart.doomthree.entity;

import org.briarheart.doomthree.AbstractMap;

/**
 * @author Roman Chigvintsev
 */
public class WorldSpawn extends Entity {
    public WorldSpawn(String entityBody) {
        super(entityBody);
    }

    @Override
    public void visit(AbstractMap map) {
    }

    @Override
    public String toJson() {
        return "";
    }

    @Override
    protected void parse(String body) {

    }
}
