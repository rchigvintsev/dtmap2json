package org.briarheart.doomthree.map.area.surface.physics;

import org.briarheart.doomthree.map.area.surface.Surface;
import org.briarheart.doomthree.map.area.surface.physics.body.Body;

/**
 * @author Roman Chigvintsev
 */
public interface CollisionModelBuildingStrategy {
    Body createBody(Surface surface, PhysicsMaterial physicsMaterial);
}
