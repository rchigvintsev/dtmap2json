package org.briarheart.doomthree.util;

/**
 * @author Roman Chigvintsev
 */
public class Vector3 {
    public final double x;
    public final double y;
    public final double z;

    public Vector3() {
        this(0.0, 0.0, 0.0);
    }

    public Vector3(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector3(Vector2 other) {
        this(other.x, other.y, 0.0);
    }

    public Vector3(Vector3 other) {
        this(other.x, other.y, other.z);
    }


    public static Vector3 fromString(String s) {
        String[] split = s.split(" ");
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[0]);
        return new Vector3(x, y, z);
    }

    public Vector3 sub(Vector3 v) {
        return new Vector3(x - v.x, y - v.y, z - v.z);
    }

    public static Vector3 subVectors(Vector3 a, Vector3 b) {
        return new Vector3(a.x - b.x, a.y - b.y, a.z - b.z);
    }

    public static Vector3 crossVectors(Vector3 a, Vector3 b) {
        double ax = a.x, ay = a.y, az = a.z;
        double bx = b.x, by = b.y, bz = b.z;
        return new Vector3(ay * bz - az * by, az * bx - ax * bz, ax * by - ay * bx);
    }

    public double lengthSq() {
        return x * x + y * y + z * z;
    }

    public Vector3 normalize() {
        double length = length();
        return divideScalar(length == 0 ? 1 : length);
    }

    public double length() {
        return Math.sqrt(x * x + y * y + z * z);
    }

    public Vector3 divideScalar(double scalar) {
        return multiplyScalar(1 / scalar);
    }

    public Vector3 multiplyScalar(double scalar) {
        return new Vector3(x * scalar, y * scalar, z * scalar);
    }

    public Vector3 invert() {
        return new Vector3(x * -1.0, y * -1.0, z * -1.0);
    }

    public Vector3 add(Vector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    public Vector3 add(IntVector3 v) {
        return new Vector3(x + v.x, y + v.y, z + v.z);
    }

    public double angleTo(Vector3 v) {
        double theta = dot(v) / Math.sqrt(lengthSq() * v.lengthSq());
        return Math.acos(clamp(theta, -1, 1));
    }

    public double dot(Vector3 v) {
        return x * v.x + y * v.y + z * v.z;
    }

    public static double clamp(double value, double min, double max) {
        return Math.max(min, Math.min(max, value));
    }

    public Vector3 projectOnPlane(Vector3 planeNormal) {
        Vector3 v1 = new Vector3(this);
        v1 = v1.projectOnVector(planeNormal);
        return sub(v1);
    }

    public Vector3 projectOnVector(Vector3 vector) {
        double scalar = vector.dot(this) / vector.lengthSq();
        return new Vector3(vector).multiplyScalar(scalar);
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

        Vector3 vector3 = (Vector3) o;

        if (Double.compare(vector3.x, x) != 0) return false;
        if (Double.compare(vector3.y, y) != 0) return false;
        return Double.compare(vector3.z, z) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(z);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    public Vector3 copy() {
        return new Vector3(this);
    }

    public Vector3 applyMatrix4(Matrix4 m) {
        double x = this.x, y = this.y, z = this.z;
        double[] e = m.elements;
        double w = 1 / (e[3] * x + e[7] * y + e[11] * z + e[15]);
        return new Vector3(
                (e[0] * x + e[4] * y + e[8] * z + e[12]) * w,
                (e[1] * x + e[5] * y + e[9] * z + e[13]) * w,
                (e[2] * x + e[6] * y + e[10] * z + e[14]) * w
        );
    }

    public double distanceTo(Vector3 v) {
        return Math.sqrt(distanceToSquared(v));
    }

    public double distanceToSquared(Vector3 v) {
        double dx = x - v.x, dy = y - v.y, dz = z - v.z;
        return dx * dx + dy * dy + dz * dz;
    }

    public Vector2 toVector2() {
        return new Vector2(x, y);
    }

    public Vector3 worldToLocal(Matrix4 worldMatrix) {
        Matrix4 m1 = new Matrix4();
        return applyMatrix4(m1.getInverse(worldMatrix));
    }

    public Vector3 localToWorld(Matrix4 worldMatrix) {
        return applyMatrix4(worldMatrix);
    }
}
