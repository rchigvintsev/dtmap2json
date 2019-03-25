package org.briarheart.doomthree.map.entity;

import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.util.Vector3;

/**
 * @author Roman Chigvintsev
 */
public class InfoPlayerStart extends Entity {
    private Vector3 position;
    private Vector3 rotation;

    public InfoPlayerStart(String entityBody) {
        super(entityBody);
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setRotation(Vector3 rotation) {
        this.rotation = rotation;
    }

    @Override
    public boolean visit(AbstractMap map, boolean lastAttempt) {
        map.setInfoPlayerStart(this);
        return true;
    }

    @Override
    protected void parse(String body) {
        position = parseOrigin(body);
        rotation = new Vector3();
    }

    @Override
    public String toJson() {
        return "\"player\":{\"position\":" + position + ",\"rotation\":" + rotation + "}";
    }
}
