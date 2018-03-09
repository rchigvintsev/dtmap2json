package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.model.surface.Surface;
import org.briarheart.doomthree.model.surface.physics.body.Body;

import java.util.Collection;

/**
 * @author Roman Chigvintsev
 */
public interface CollisionModelBuildingStrategy {
    Collection<? extends Body> createBodies(Surface surface, PhysicsMaterial physicsMaterial);
}
