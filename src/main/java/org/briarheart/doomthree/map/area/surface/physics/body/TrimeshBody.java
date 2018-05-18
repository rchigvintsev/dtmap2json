package org.briarheart.doomthree.map.area.surface.physics.body;

import org.briarheart.doomthree.map.area.surface.physics.PhysicsMaterial;

/**
 * @author Roman Chigvintsev
 */
public class TrimeshBody extends AbstractBody {
    private final double[] vertices;
    private final int[] indices;

    public TrimeshBody(double[] vertices, int[] indices, PhysicsMaterial material) {
        super(material);
        this.vertices = vertices;
        this.indices = indices;
    }

    @Override
    public String toJson() {
        StringBuilder json = new StringBuilder("{")
                .append("\"mass\":0,")
                .append("\"material\":\"").append(getMaterial().toString()).append("\",")
                .append("\"shapes\":[{")
                .append("\"type\":\"trimesh\",")
                .append("\"vertices\":[");
        for (int i = 0; i < vertices.length; i++) {
            if (i > 0)
                json.append(",");
            json.append(vertices[i]);
        }
        json.append("],").append("\"indices\":[");
        for (int i = 0; i < indices.length; i++) {
            if (i > 0)
                json.append(",");
            json.append(indices[i]);
        }
        json.append("]}]}");
        return json.toString();
    }

    @Override
    public String toString() {
        return toJson();
    }
}
