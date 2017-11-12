package org.briarheart.doomthree.util;

/**
 * @author Roman Chigvintsev
 */
public class Vector2 {
    public final double x;
    public final double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public String toString() {
        return toJson();
    }

    public String toJson() {
        return "[" + x + "," + y + "]";
    }
}
