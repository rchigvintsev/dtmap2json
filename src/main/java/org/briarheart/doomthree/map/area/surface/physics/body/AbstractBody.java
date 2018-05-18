package org.briarheart.doomthree.map.area.surface.physics.body;

import org.briarheart.doomthree.map.area.surface.physics.PhysicsMaterial;
import org.briarheart.doomthree.util.Vector3;

/**
 * @author Roman Chigvintsev
 */
public abstract class AbstractBody implements Body {
    private final PhysicsMaterial material;

    private Vector3 position;

    protected AbstractBody(PhysicsMaterial material) {
        this(null, material);
    }

    protected AbstractBody(Vector3 position, PhysicsMaterial material) {
        this.position = position;
        this.material = material;
    }

    public PhysicsMaterial getMaterial() {
        return material;
    }

    @Override
    public Vector3 getPosition() {
        return position;
    }

    @Override
    public void setPosition(Vector3 position) {
        this.position = position;
    }
}
