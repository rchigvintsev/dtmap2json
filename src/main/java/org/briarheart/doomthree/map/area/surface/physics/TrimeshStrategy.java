package org.briarheart.doomthree.map.area.surface.physics;

import org.briarheart.doomthree.map.area.surface.Face;
import org.briarheart.doomthree.map.area.surface.Surface;
import org.briarheart.doomthree.map.area.surface.Vertex;
import org.briarheart.doomthree.map.area.surface.physics.body.Body;
import org.briarheart.doomthree.map.area.surface.physics.body.TrimeshBody;

/**
 * @author Roman Chigvintsev
 */
public class TrimeshStrategy implements CollisionModelBuildingStrategy {
    @Override
    public Body createBody(Surface surface, PhysicsMaterial physicsMaterial) {
        double[] vertices = new double[surface.getVertices().length * 3];
        for (int i = 0, j = 0; i < surface.getVertices().length; i++, j += 3) {
            Vertex v = surface.getVertices()[i];
            vertices[j] = v.position.x;
            vertices[j + 1] = v.position.y;
            vertices[j + 2] = v.position.z;
        }

        int[] indices = new int[surface.getFaces().length * 3];
        for (int i = 0, j = 0; i < surface.getFaces().length; i++, j += 3) {
            Face f = surface.getFaces()[i];
            indices[j] = f.a;
            indices[j + 1] = f.b;
            indices[j + 2] = f.c;
        }

        return new TrimeshBody(vertices, indices, physicsMaterial);
    }
}
