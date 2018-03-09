package org.briarheart.doomthree.map.game;

import org.briarheart.doomthree.model.Model;
import org.briarheart.doomthree.model.surface.Surface;

/**
 * @author Roman Chigvintsev
 */
public class Site3Surface extends Surface {
    public Site3Surface(Model model, String surfaceBody) {
        super(model, surfaceBody);
    }

    @Override
    protected boolean isNeedToCreateCollisionModel(String materialName) {
        boolean result = super.isNeedToCreateCollisionModel(materialName);
        if (result && (materialName.contains("/sfx/") || materialName.contains("/outside/")))
            result = false;
        return result;
    }
}
