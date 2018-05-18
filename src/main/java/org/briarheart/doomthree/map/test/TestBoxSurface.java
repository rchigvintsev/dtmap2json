package org.briarheart.doomthree.map.test;

import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.map.area.surface.Surface;
import org.briarheart.doomthree.map.area.surface.physics.PhysicsMaterial;

/**
 * @author Roman Chigvintsev
 */
public class TestBoxSurface extends Surface {
    public TestBoxSurface(Area area, String surfaceBody) {
        super(area, surfaceBody);
    }

    @Override
    protected PhysicsMaterial getPhysicsMaterial(String materialName) {
        if (materialName.equals("textures/base_wall/lfwall27b"))
            return PhysicsMaterial.FLOOR;
        return super.getPhysicsMaterial(materialName);
    }
}
