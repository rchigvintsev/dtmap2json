package org.briarheart.doomthree.util;

import org.apache.commons.math3.util.Precision;

/**
 * @author Roman Chigvintsev
 */
public class Vector2 {
    private static final double ERROR = 0.00001;

    public final double x;
    public final double y;

    public Vector2(double x, double y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Vector2 vector2 = (Vector2) o;
        return Precision.equals(vector2.x, x, ERROR) && Precision.equals(vector2.y, y, ERROR);
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return toJson();
    }

    public String toJson() {
        return "[" + x + "," + y + "]";
    }
}
