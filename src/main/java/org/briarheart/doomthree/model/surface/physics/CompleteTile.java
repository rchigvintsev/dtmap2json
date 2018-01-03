package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.util.Rectangle2D;

/**
 * @author Roman Chigvintsev
 */
public class CompleteTile implements Tile {
    private final Rectangle2D rectangle2D;

    public CompleteTile(Rectangle2D rectangle2D) {
        this.rectangle2D = rectangle2D;
    }

    public Rectangle2D getRectangle2D() {
        return rectangle2D;
    }

    @Override
    public void apply(BoxBodyCollector boxCollector) {
        boxCollector.visitCompleteTile(this);
    }
}
