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

    public static boolean isSoil(String materialName) {
        return materialName.contains("skysand");
    }

    public static boolean isSkyBox(String materialName) {
        return materialName.contains("skies");
    }
}
