package org.briarheart.doomthree.map.test;

import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.map.area.surface.Surface;

/**
 * @author Roman Chigvintsev
 */
public class TestBox extends AbstractMap {
    public static final String NAME = "testmaps/test_box";

    public TestBox() {
        super(NAME, null);
    }

    @Override
    public Surface newSurface(Area area, String surfaceBody) {
        return new TestBoxSurface(area, surfaceBody);
    }
}
