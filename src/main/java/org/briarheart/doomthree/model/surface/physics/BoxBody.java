package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.util.Quaternion;
import org.briarheart.doomthree.util.Vector3;

/**
 * @author Roman Chigvintsev
 */
public class BoxBody {
    public final Vector3 origin;
    public final Vector3 position;
    public final Vector3 size;
    public final Quaternion quaternion;
    public final Vector3 normal;
    public final PhysicsMaterial material;

    public BoxBody(Vector3 origin, Vector3 size, Vector3 normal, Quaternion quaternion) {
        this(origin, size, normal, quaternion, PhysicsMaterial.DEFAULT);
    }

    public BoxBody(Vector3 origin, Vector3 size, Vector3 normal, Quaternion quaternion, PhysicsMaterial material) {
        this.size = size;
        this.origin = origin;
        this.position = origin.add(normal.invert().multiplyScalar(size.z / 2.0));
        this.normal = normal;
        this.quaternion = quaternion;
        this.material = material;
    }

    public String toJson() {
        return "{" +
                "\"mass\":0," +
                "\"material\":\"" + material.toString() + "\"," +
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
