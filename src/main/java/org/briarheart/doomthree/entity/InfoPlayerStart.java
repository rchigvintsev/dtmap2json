package org.briarheart.doomthree.entity;

import org.briarheart.doomthree.Map;
import org.briarheart.doomthree.util.Vector3;

/**
 * @author Roman Chigvintsev
 */
public class InfoPlayerStart extends Entity {
    private Vector3 position;

    public InfoPlayerStart(String entityBody) {
        super(entityBody);
    }

    @Override
    public void visit(Map map) {
        map.setInfoPlayerStart(this);
    }

    @Override
    protected void parse(String body) {
        position = parseOrigin(body);
    }

    @Override
    public String toJson() {
        return "\"player\":{\"position\":" + position + "}";
    }
}
