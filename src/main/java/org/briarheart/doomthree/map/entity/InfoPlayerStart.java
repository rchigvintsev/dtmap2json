package org.briarheart.doomthree.map.entity;

import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.util.Vector3;

/**
 * @author Roman Chigvintsev
 */
public class InfoPlayerStart extends Entity {
    private Vector3 position;

    public InfoPlayerStart(String entityBody) {
        super(entityBody);
    }

    public Vector3 getPosition() {
        return position;
    }

    @Override
    public boolean visit(AbstractMap map, boolean warnIfFailed) {
        map.setInfoPlayerStart(this);
        return true;
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
