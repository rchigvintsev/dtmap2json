package org.briarheart.doomthree.util;

/**
 * @author Roman Chigvintsev
 */
public class Rectangle {
    private final double width;
    private final double height;
    private final Vector2 position;
    private final Vector2 bottomLeft;
    private final Vector2 upperLeft;
    private final Vector2 upperRight;
    private final Vector2 bottomRight;

    public Rectangle(Vector2 size, Vector2 position) {
        this(size.x, size.y, position);
    }

    public Rectangle(double width, double height, Vector2 position) {
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

    public Rectangle[] split(double areaThreshold) {
        if (width * height < areaThreshold)
            return null;

        Rectangle[] result = new Rectangle[4];

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
            result[i] = new Rectangle(halfWidth, halfHeight, position);
        }

        return result;
    }

    public Rectangle expand(ExpansionDirection direction) {
        switch (direction) {
            case UP: {
                Vector2 position = new Vector2(this.position.x, this.position.y + height / 2);
                return new Rectangle(width, height * 2, position);
            }
            case DOWN: {
                Vector2 position = new Vector2(this.position.x, this.position.y - height / 2);
                return new Rectangle(width, height * 2, position);
            }
            case RIGHT: {
                Vector2 position = new Vector2(this.position.x + width / 2, this.position.y);
                return new Rectangle(width * 2, height, position);
            }
            case LEFT: {
                Vector2 position = new Vector2(this.position.x - width / 2, this.position.y);
                return new Rectangle(width * 2, height, position);
            }
            default:
                throw new RuntimeException("It was not supposed to happen!");
        }
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

    public enum ExpansionDirection {UP, DOWN, RIGHT, LEFT}
}
