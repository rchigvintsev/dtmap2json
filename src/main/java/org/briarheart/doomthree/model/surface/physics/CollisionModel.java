package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.model.surface.Surface;
import org.briarheart.doomthree.model.surface.physics.body.Body;

/**
 * @author Roman Chigvintsev
 */
public class CollisionModel {
    private final Body body;

    private CollisionModel(Body body) {
        this.body = body;
    }

    public static CollisionModel newCollisionModel(Surface surface,
                                                   PhysicsMaterial physicsMaterial,
                                                   CollisionModelBuildingStrategy strategy) {
        Body body = strategy.createBody(surface, physicsMaterial);
        return body == null ? null : new CollisionModel(body);
    }

    public Body getBody() {
        return body;
    }

    public String toJson() {
        return "{\"bodies\":[" + body.toJson() + "]}";
    }

    @Override
    public String toString() {
        return toJson();
    }
}
