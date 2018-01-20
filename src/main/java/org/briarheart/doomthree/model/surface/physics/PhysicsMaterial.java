package org.briarheart.doomthree.model.surface.physics;

/**
 * @author Roman Chigvintsev
 */
public enum PhysicsMaterial {
    DEFAULT("default"), FLOOR("floor");

    private final String name;

    PhysicsMaterial(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
