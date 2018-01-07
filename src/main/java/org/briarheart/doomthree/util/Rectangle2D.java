package org.briarheart.doomthree.util;

import org.apache.commons.lang3.ArrayUtils;

/**
 * @author Roman Chigvintsev
 */
public class Rectangle2D {
    private final double width;
    private final double height;
    private final Vector2 position;
    private final Vector2[] vertices = new Vector2[4];

    public Rectangle2D(Vector2 size, Vector2 position) {
        this(size.x, size.y, position);
    }

    public Rectangle2D(double width, double height, Vector2 position) {
        this.width = width;
        this.height = height;
        this.position = position;

        double halfWidth = width / 2;
        double halfHeight = height / 2;

        this.vertices[0] = new Vector2(position.x - halfWidth, position.y - halfHeight);
        this.vertices[1] = new Vector2(position.x - halfWidth, position.y + halfHeight);
        this.vertices[2] = new Vector2(position.x + halfWidth, position.y + halfHeight);
        this.vertices[3] = new Vector2(position.x + halfWidth, position.y - halfHeight);
    }

    public Rectangle2D[] split(double areaThreshold) {
        if (width * height < areaThreshold)
            return null;

        Rectangle2D[] result = new Rectangle2D[4];

        double halfWidth = width / 2;
        double halfHeight = height / 2;

        for (int i = 0; i < 4; i++) {
            Vector2 position = null;
            switch (i) {
                case 0:
                    position = new Vector2(this.position.x - halfWidth / 2, this.position.y - halfHeight / 2);
                    break;
                case 1:
                    position = new Vector2(this.position.x - halfWidth / 2, this.position.y + halfHeight / 2);
                    break;
                case 2:
                    position = new Vector2(this.position.x + halfWidth / 2, this.position.y + halfHeight / 2);
                    break;
                case 3:
                    position = new Vector2(this.position.x + halfWidth / 2, this.position.y - halfHeight / 2);
                    break;
            }
            result[i] = new Rectangle2D(halfWidth, halfHeight, position);
        }

        return result;
    }

    public Rectangle2D merge(Rectangle2D other) {
        if (this.getUpperLeft().equals(other.getBottomLeft()) && this.getUpperRight().equals(other.getBottomRight())) {
            // Merge upwards
            Vector2 position = new Vector2(this.position.x, this.position.y + other.height / 2);
            return new Rectangle2D(width, height + other.height, position);
        }

        if (this.getBottomLeft().equals(other.getUpperLeft()) && this.getBottomRight().equals(other.getUpperRight())) {
            // Merge downwards
            Vector2 position = new Vector2(this.position.x, this.position.y - other.height / 2);
            return new Rectangle2D(width, height + other.height, position);
        }

        if (this.getUpperRight().equals(other.getUpperLeft()) && this.getBottomRight().equals(other.getBottomLeft())) {
            // Merge to the right
            Vector2 position = new Vector2(this.position.x + other.width / 2, this.position.y);
            return new Rectangle2D(width + other.width, height, position);
        }

        if (this.getUpperLeft().equals(other.getUpperRight()) && this.getBottomLeft().equals(other.getBottomRight())) {
            // Merge to the left
            Vector2 position = new Vector2(this.position.x - other.width / 2, this.position.y);
            return new Rectangle2D(width + other.width, height, position);
        }

        throw new IllegalArgumentException("Rectangles cannot be merged");
    }

    public boolean hasAtLeastTwoCommonVerticesWith(Rectangle2D other) {
        int i = 0, j = 0;
        while (i < 4 && j < 2)
            if (ArrayUtils.contains(this.vertices, other.vertices[i++]))
                j++;
        return j > 1;
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public Vector2 getPosition() {
        return position;
    }

    public Vector2 getBottomLeft() {
        return vertices[0];
    }

    public Vector2 getUpperLeft() {
        return vertices[1];
    }
    
    public Vector2 getUpperRight() {
        return vertices[2];
    }

    public Vector2 getBottomRight() {
        return vertices[3];
    }
}
