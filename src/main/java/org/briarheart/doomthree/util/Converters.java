package org.briarheart.doomthree.util;

/**
 * @author Roman Chigvintsev
 */
public abstract class Converters {
    private Converters() {
        //no instance
    }

    public static double degreesToRadians(double degrees) {
         return degrees * Math.PI / 180.0d;
    }

    public static double radiansToDegrees(double radians) {
        return radians * 180.0d / Math.PI;
    }
}
