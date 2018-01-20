package org.briarheart.doomthree.map.test;

import org.briarheart.doomthree.model.Model;
import org.briarheart.doomthree.model.surface.Surface;
import org.briarheart.doomthree.model.surface.physics.PhysicsMaterial;

/**
 * @author Roman Chigvintsev
 */
public class TestBoxSurface extends Surface {
    public TestBoxSurface(Model model, String surfaceBody) {
        super(model, surfaceBody);
    }

    @Override
    protected PhysicsMaterial getPhysicsMaterial(String materialName) {
        if (materialName.equals("textures/base_wall/lfwall27b"))
            return PhysicsMaterial.FLOOR;
        return super.getPhysicsMaterial(materialName);
    }
}
