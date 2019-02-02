package org.briarheart.doomthree.map.game;

import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.map.area.surface.Surface;
import org.briarheart.doomthree.map.entity.InfoPlayerStart;
import org.briarheart.doomthree.util.Converters;
import org.briarheart.doomthree.util.Vector3;

/**
 * @author Roman Chigvintsev
 */
public class Site3 extends AbstractMap {
    public static final String NAME = "game/site3";

    public Site3(String areaFilter) {
        super(NAME, areaFilter);
    }

    @Override
    public Surface newSurface(Area area, String surfaceBody) {
        return new Site3Surface(area, surfaceBody);
    }

    @Override
    public void setInfoPlayerStart(InfoPlayerStart infoPlayerStart) {
        super.setInfoPlayerStart(infoPlayerStart);
        infoPlayerStart.setRotation(new Vector3(0, Converters.degreesToRadians(180), 0));
    }
}