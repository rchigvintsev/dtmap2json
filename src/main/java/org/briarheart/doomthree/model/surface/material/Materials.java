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
}
