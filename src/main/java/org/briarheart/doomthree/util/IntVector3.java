package org.briarheart.doomthree.util;

/**
 * @author Roman Chigvintsev
 */
public class IntVector3 {
    public final int x;
    public final int y;
    public final int z;

    public IntVector3() {
        this(0, 0, 0);
    }

    public IntVector3(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public double angleTo(IntVector3 v) {
        double theta = dot(v) / Math.sqrt(lengthSq() * v.lengthSq());
        return Math.acos(clamp(theta, - 1, 1));
    }

    public int dot(IntVector3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public int lengthSq() {
        return x * x + y * y + z * z;
    }

    @Override
    public String toString() {
        return toJson();
    }

    public String toJson() {
        return "[" + x + "," + y + "," + z + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IntVector3 that = (IntVector3) o;

        if (x != that.x) return false;
        if (y != that.y) return false;
        return z == that.z;
    }

    @Override
    public int hashCode() {
        int result = x;
        result = 31 * result + y;
        result = 31 * result + z;
        return result;
    }
}
