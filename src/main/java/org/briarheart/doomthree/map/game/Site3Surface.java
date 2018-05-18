package org.briarheart.doomthree.map.game;

import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.map.area.surface.Surface;

/**
 * @author Roman Chigvintsev
 */
public class Site3Surface extends Surface {
    public Site3Surface(Area area, String surfaceBody) {
        super(area, surfaceBody);
    }

    @Override
    protected boolean isNeedToCreateCollisionModel(String materialName) {
        boolean result = super.isNeedToCreateCollisionModel(materialName);
        if (result && (materialName.contains("/sfx/") || materialName.contains("/outside/")))
            result = false;
        return result;
    }
}
