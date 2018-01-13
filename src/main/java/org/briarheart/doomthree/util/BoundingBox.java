package org.briarheart.doomthree.util;

import org.apache.commons.math3.util.Precision;

/**
 * @author Roman Chigvintsev
 */
public class BoundingBox {
    private static final double ERROR = 1.0;

    private Double minX, maxX;
    private Double minY, maxY;
    private Double minZ, maxZ;

    public boolean contains(Vector3 point) {
        if (point.x < minX || point.x > maxX)
            return false;
        if (point.y < minY || point.y > maxY)
            return false;
        if (point.z < minZ || point.z > maxZ)
            return false;
        return true;
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
}
