package org.briarheart.doomthree.util;

/**
 * @author Roman Chigvintsev
 */
public class Rectangle2D {
    private final double width;
    private final double height;
    private final Vector2 position;
    private final Vector2 bottomLeft;
    private final Vector2 upperLeft;
    private final Vector2 upperRight;
    private final Vector2 bottomRight;

    public Rectangle2D(Vector2 size, Vector2 position) {
        this(size.x, size.y, position);
    }

    public Rectangle2D(double width, double height, Vector2 position) {
        this.width = width;
        this.height = height;
        this.position = position;

        double halfWidth = width / 2;
        double halfHeight = height / 2;

        this.bottomLeft = new Vector2(position.x - halfWidth, position.y - halfHeight);
        this.upperLeft = new Vector2(position.x - halfWidth, position.y + halfHeight);
        this.upperRight = new Vector2(position.x + halfWidth, position.y + halfHeight);
        this.bottomRight = new Vector2(position.x + halfWidth, position.y - halfHeight);
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
        if (this.upperLeft.equals(other.bottomLeft) && this.upperRight.equals(other.bottomRight)) {
            // Merge upwards
            Vector2 position = new Vector2(this.position.x, this.position.y + other.height / 2);
            return new Rectangle2D(width, height + other.height, position);
        }

        if (this.bottomLeft.equals(other.upperLeft) && this.bottomRight.equals(other.upperRight)) {
            // Merge downwards
            Vector2 position = new Vector2(this.position.x, this.position.y - other.height / 2);
            return new Rectangle2D(width, height + other.height, position);
        }

        if (this.upperRight.equals(other.upperLeft) && this.bottomRight.equals(other.bottomLeft)) {
            // Merge to the right
            Vector2 position = new Vector2(this.position.x + other.width / 2, this.position.y);
            return new Rectangle2D(width + other.width, height, position);
        }

        if (this.upperLeft.equals(other.upperRight) && this.bottomLeft.equals(other.bottomRight)) {
            // Merge to the left
            Vector2 position = new Vector2(this.position.x - other.width / 2, this.position.y);
            return new Rectangle2D(width + other.width, height, position);
        }

        throw new IllegalArgumentException("Rectangles cannot be merged");
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
        return bottomLeft;
    }

    public Vector2 getUpperLeft() {
        return upperLeft;
    }

    public Vector2 getUpperRight() {
        return upperRight;
    }

    public Vector2 getBottomRight() {
        return bottomRight;
    }
}
