package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.util.Rectangle2D;
import org.briarheart.doomthree.util.Vector2;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

/**
 * @author Roman Chigvintsev
 */
class TileContainer implements Tile, Iterable<Tile> {
    private final List<Tile> children = new ArrayList<>(4);

    public TileContainer(Rectangle2D rectangle, double areaThreshold, Predicate<Vector2> pointInPolygonPredicate) {
        Rectangle2D[] subRectangles = rectangle.split(areaThreshold);
        if (subRectangles != null) {
            for (int i = 0; i < subRectangles.length; i++) {
                Rectangle2D r = subRectangles[i];
                int j = 0;
                if (pointInPolygonPredicate.test(r.getBottomLeft()))  j++;
                if (pointInPolygonPredicate.test(r.getUpperLeft()))   j++;
                if (pointInPolygonPredicate.test(r.getUpperRight()))  j++;
                if (pointInPolygonPredicate.test(r.getBottomRight())) j++;
                if (j < 4) {
                    subRectangles[i] = null;
                    if (j > 0)
                        children.add(new TileContainer(r, areaThreshold, pointInPolygonPredicate));
                }
            }

            if (!merge(subRectangles, 0, 1) && !merge(subRectangles, 0, 3) && !merge(subRectangles, 2, 3))
                merge(subRectangles, 1, 2);

            for (Rectangle2D subRectangle : subRectangles)
                if (subRectangle != null)
                    children.add(new CompleteTile(subRectangle));
        }
    }

    @Override
    public Iterator<Tile> iterator() {
        return children.iterator();
    }

    @Override
    public void apply(BoxBodyCollector boxCollector) {
        boxCollector.visitTileContainer(this);
    }

    private boolean merge(Rectangle2D[] rectangles, int i, int j) {
        if (rectangles[i] != null && rectangles[j] != null) {
            Rectangle2D merged = rectangles[i].merge(rectangles[j]);
            if (merged != null) {
                rectangles[i] = merged;
                rectangles[j] = null;
                return true;
            }
        }
        return false;
    }
}
