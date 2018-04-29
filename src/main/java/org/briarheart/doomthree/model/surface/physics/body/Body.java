package org.briarheart.doomthree.model.surface.physics.body;

import org.briarheart.doomthree.util.Vector3;

/**
 * @author Roman Chigvintsev
 */
public interface Body {
    Vector3 getPosition();

    void setPosition(Vector3 position);

    String toJson();
}
