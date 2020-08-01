package org.briarheart.doomthree.util;

import org.apache.commons.math3.util.Precision;

/**
 * @author Roman Chigvintsev
 */
public class BoundingBox {
    private static final double ERROR = 1.0;
    private static final double RANGE_ERROR = 20.0;

    private Double minX, maxX;
    private Double minY, maxY;
    private Double minZ, maxZ;

    public boolean contains(Vector3 point) {
        return checkRange(minX, maxX, point.x)
                && checkRange(minY, maxY, point.y)
                && checkRange(minZ, maxZ, point.z);
    }

    public boolean contains(BoundingBox other) {
        if (other.minX < minX || other.maxX > maxX)
            return false;
        if (other.minY < minY || other.maxY > maxY)
            return false;
        if (other.minZ < minZ || other.maxZ > maxZ)
            return false;
        return true;
    }

    public boolean overlaps(BoundingBox other) {
        return Precision.compareTo(this.minX, other.maxX, ERROR) < 0
                && Precision.compareTo(this.maxX, other.minX, ERROR) > 0
                && Precision.compareTo(this.minY, other.maxY, ERROR) < 0
                && Precision.compareTo(this.maxY, other.minY, ERROR) > 0;
    }

    public void checkBoundaries(Vector2 point) {
        if (minX == null || point.x < minX)
            minX = point.x;
        if (maxX == null || point.x > maxX)
            maxX = point.x;
        if (minY == null || point.y < minY)
            minY = point.y;
        if (maxY == null || point.y > maxY)
            maxY = point.y;
    }

    public void checkBoundaries(Vector3 point) {
        if (minX == null || point.x < minX)
            minX = point.x;
        if (maxX == null || point.x > maxX)
            maxX = point.x;
        if (minY == null || point.y < minY)
            minY = point.y;
        if (maxY == null || point.y > maxY)
            maxY = point.y;
        if (minZ == null || point.z < minZ)
            minZ = point.z;
        if (maxZ == null || point.z > maxZ)
            maxZ = point.z;
    }

    public void checkBoundaries(BoundingBox boundingBox) {
        if (boundingBox.minX != null && (minX == null || boundingBox.minX < minX))
            minX = boundingBox.minX;
        if (boundingBox.maxX != null && (maxX == null || boundingBox.maxX > maxX))
            maxX = boundingBox.maxX;
        if (boundingBox.minY != null && (minY == null || boundingBox.minY < minY))
            minY = boundingBox.minY;
        if (boundingBox.maxY != null && (maxY == null || boundingBox.maxY > maxY))
            maxY = boundingBox.maxY;
        if (boundingBox.minZ != null && (minZ == null || boundingBox.minZ < minZ))
            minZ = boundingBox.minZ;
        if (boundingBox.maxZ != null && (maxZ == null || boundingBox.maxZ > maxZ))
            maxZ = boundingBox.maxZ;
    }

    public void add(Vector3 v) {
        this.minX = this.minX == null ? v.x : this.minX + v.x;
        this.maxX = this.maxX == null ? v.x : this.maxX + v.x;
        this.minY = this.minY == null ? v.y : this.minY + v.y;
        this.maxY = this.maxY == null ? v.y : this.maxY + v.y;
        this.minZ = this.minZ == null ? v.z : this.minZ + v.z;
        this.maxZ = this.maxZ == null ? v.z : this.maxZ + v.z;
    }

    public Double getMinX() {
        return minX;
    }

    public Double getMaxX() {
        return maxX;
    }

    public Double getMinY() {
        return minY;
    }

    public Double getMaxY() {
        return maxY;
    }

    public Double getMinZ() {
        return minZ;
    }

    public Double getMaxZ() {
        return maxZ;
    }

    public double getWidth() {
        return maxX - minX;
    }

    public double getHeight() {
        return maxY - minY;
    }

    public double getDepth() {
        return maxZ - minZ;
    }

    public String toJson() {
        return "[" + zeroIfNull(minX) + ","
                + zeroIfNull(maxX) + ","
                + zeroIfNull(minY) + ","
                + zeroIfNull(maxY) + ","
                + zeroIfNull(minZ) + ","
                + zeroIfNull(maxZ) + "]";
    }

    @Override
    public String toString() {
        return toJson();
    }

    private static boolean checkRange(Double min, Double max, double value) {
        return min != null && value >= min - RANGE_ERROR && max != null && value <= max + RANGE_ERROR;
    }

    private static Double zeroIfNull(Double v) {
        return v == null ? 0.0 : v;
    }
}
