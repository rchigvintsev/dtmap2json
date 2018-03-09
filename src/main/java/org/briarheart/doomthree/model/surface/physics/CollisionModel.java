package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.model.surface.Surface;
import org.briarheart.doomthree.model.surface.physics.body.Body;

import java.util.Collection;

/**
 * @author Roman Chigvintsev
 */
public class CollisionModel {
    private final Collection<? extends Body> bodies;

    private CollisionModel(Collection<? extends Body> bodies) {
        this.bodies = bodies;
    }

    public static CollisionModel newCollisionModel(Surface surface,
                                                   PhysicsMaterial physicsMaterial,
                                                   CollisionModelBuildingStrategy strategy) {
        return new CollisionModel(strategy.createBodies(surface, physicsMaterial));
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\"bodies\":[");
        int i = 0;
        for (Body body : bodies) {
            if (i > 0)
                json.append(",");
            json.append(body.toJson());
            i++;
        }
        return json.append("]}").toString();
    }

    @Override
    public String toString() {
        return toJson();
    }
}
