package org.briarheart.doomthree.map.game;

import org.briarheart.doomthree.AbstractMap;
import org.briarheart.doomthree.model.Model;
import org.briarheart.doomthree.model.surface.Surface;

/**
 * @author Roman Chigvintsev
 */
public class Site3 extends AbstractMap {
    public static final String NAME = "game/site3";

    public Site3(String areaFilter) {
        super(NAME, areaFilter);
    }

    @Override
    public Surface newSurface(Model model, String surfaceBody) {
        return new Site3Surface(model, surfaceBody);
    }
}
