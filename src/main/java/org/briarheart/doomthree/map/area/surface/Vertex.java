package org.briarheart.doomthree.map.area.surface;

import org.briarheart.doomthree.util.Vector2;
import org.briarheart.doomthree.util.Vector3;

/**
 * @author Roman Chigvintsev
 */
public class Vertex {
    public final Vector3 position;
    public final Vector2 uv;
    public final Vector3 normal;

    public Vertex(Vector3 position, Vector2 uv, Vector3 normal) {
        this.position = position;
        this.uv = uv;
        this.normal = normal;
    }

    @Override
    public String toString() {
        return toJson();
    }

    public String toJson() {
        return position.toJson();
    }
}
