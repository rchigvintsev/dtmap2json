package org.briarheart.doomthree.model.surface.material;

/**
 * @author Roman Chigvintsev
 */
public class Materials {
    private Materials() {
        //no instance
    }

    public static boolean isDecal(String materialName) {
        return materialName.contains("/decals/");
    }

    public static boolean isFloor(String materialName) {
        return materialName.contains("/base_floor/");
    }
}
