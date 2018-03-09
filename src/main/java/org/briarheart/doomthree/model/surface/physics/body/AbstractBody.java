package org.briarheart.doomthree.model.surface.physics.body;

import org.briarheart.doomthree.model.surface.physics.PhysicsMaterial;

/**
 * @author Roman Chigvintsev
 */
public abstract class AbstractBody implements Body {
    private final PhysicsMaterial material;

    protected AbstractBody(PhysicsMaterial material) {
        this.material = material;
    }

    public PhysicsMaterial getMaterial() {
        return material;
    }
}
