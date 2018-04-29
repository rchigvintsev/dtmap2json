package org.briarheart.doomthree.model.surface.physics.body;

import org.briarheart.doomthree.model.surface.physics.PhysicsMaterial;
import org.briarheart.doomthree.util.Quaternion;
import org.briarheart.doomthree.util.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Chigvintsev
 */
public class BoxBody extends AbstractBody {
    public final Vector3 normal;

    private final List<Shape> shapes = new ArrayList<>();

    public BoxBody(Vector3 position, Vector3 normal, PhysicsMaterial material) {
        super(position, material);
        this.normal = normal;
    }

    public List<Shape> getShapes() {
        return shapes;
    }

    @Override
    public String toJson() {
        StringBuilder json = new StringBuilder("{")
                .append("\"mass\":0,")
                .append("\"material\":\"").append(getMaterial().toString()).append("\",")
                .append("\"shapes\":[");
        for (int i = 0; i < shapes.size(); i++) {
            if (i > 0)
                json.append(",");
            Shape shape = shapes.get(i);
            json.append(shape.toJson());
        }
        return json.append("],")
                .append("\"position\":").append(getPosition().toJson())
                .append("}").toString();
    }

    @Override
    public String toString() {
        return toJson();
    }

    public static class Shape {
        public final Vector3 size;
        public final Vector3 offset;
        public final Quaternion quaternion;

        public Shape(Vector3 size, Vector3 offset, Quaternion quaternion) {
            this.size = size;
            this.offset = offset;
            this.quaternion = quaternion;
        }

        public String toJson() {
            String json = "{" +
                    "\"type\":\"box\"," +
                    "\"width\":" + size.x + "," +
                    "\"height\":" + size.y + "," +
                    "\"depth\":" + size.z;
            if (offset != null)
                json += ",\"offset\":" + offset.toJson();
            if (quaternion != null)
                json += ",\"quaternion\":" + quaternion.toJson();
            return json + "}";
        }

        @Override
        public String toString() {
            return toJson();
        }
    }
}
