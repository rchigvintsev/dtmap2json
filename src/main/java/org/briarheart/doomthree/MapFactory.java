package org.briarheart.doomthree;

import org.briarheart.doomthree.map.game.Site3;
import org.briarheart.doomthree.map.test.TestBox;

/**
 * @author Roman Chigvintsev
 */
public class MapFactory {
    private MapFactory() {
        //no instance
    }

    public static AbstractMap createMap(String name, String areaFilter) {
        switch (name) {
            case "testmaps/test_box":
                return new TestBox();
            case "game/site3":
                return new Site3(areaFilter);
            default:
                throw new IllegalArgumentException("Unsupported map: " + name);
        }
    }
}
