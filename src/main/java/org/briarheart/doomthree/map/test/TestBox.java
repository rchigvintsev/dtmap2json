package org.briarheart.doomthree.map.test;

import org.briarheart.doomthree.AbstractMap;
import org.briarheart.doomthree.model.Model;
import org.briarheart.doomthree.model.surface.Surface;

/**
 * @author Roman Chigvintsev
 */
public class TestBox extends AbstractMap {
    public static final String NAME = "testmaps/test_box";

    public TestBox() {
        super(NAME, null);
    }

    @Override
    public Surface newSurface(Model model, String surfaceBody) {
        return new TestBoxSurface(model, surfaceBody);
    }
}
