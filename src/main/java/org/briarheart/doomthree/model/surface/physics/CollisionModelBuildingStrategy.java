package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.model.surface.Surface;
import org.briarheart.doomthree.model.surface.physics.body.Body;

/**
 * @author Roman Chigvintsev
 */
public interface CollisionModelBuildingStrategy {
    Body createBody(Surface surface, PhysicsMaterial physicsMaterial);
}
