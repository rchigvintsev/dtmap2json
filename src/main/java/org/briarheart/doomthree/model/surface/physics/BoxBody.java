package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.util.Quaternion;
import org.briarheart.doomthree.util.Vector3;

/**
 * @author Roman Chigvintsev
 */
public class BoxBody {
    private final Vector3 size;
    private final Vector3 position;
    private final Quaternion quaternion;

    public BoxBody(Vector3 size, Vector3 position, Quaternion quaternion) {
        this.size = size;
        this.position = position;
        this.quaternion = quaternion;
    }

    public String toJson() {
        return "{" +
                "\"mass\":0," +
                "\"shapes\":[{" +
                "\"type\":\"box\"," +
                "\"width\":" + size.x + "," +
                "\"height\":" + size.y + "," +
                "\"depth\":" + size.z +
                "}]," +
                "\"position\":" + position.toJson() + "," +
                "\"quaternion\":" + quaternion.toJson() +
                "}";
    }

    @Override
    public String toString() {
        return toJson();
    }
}
