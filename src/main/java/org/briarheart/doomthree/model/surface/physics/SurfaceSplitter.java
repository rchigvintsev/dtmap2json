package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.model.surface.Face;
import org.briarheart.doomthree.model.surface.Surface;
import org.briarheart.doomthree.util.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * @author Roman Chigvintsev
 */
public class SurfaceSplitter {
    private final Surface surface;
    private final PhysicsMaterial physicsMaterial;
    private final Vector3 normal;
    private final double areaThreshold;
    private final double boxBodyThickness;

    public SurfaceSplitter(Surface surface,
                           PhysicsMaterial physicsMaterial,
                           Vector3 normal,
                           double areaThreshold,
                           double boxBodyThickness) {
        this.surface = surface;
        this.physicsMaterial = physicsMaterial;
        this.normal = normal;
        this.areaThreshold = areaThreshold;
        this.boxBodyThickness = boxBodyThickness;
    }

    public List<BoxBody> split(Set<Face> faces, Vector3 origin, Quaternion quaternion, Vector3 size) {
        Matrix4 worldMatrix = new Matrix4();
        worldMatrix.compose(origin, quaternion);
        Vector3 localOrigin = origin.worldToLocal(worldMatrix);
        Rectangle2D root = new Rectangle2D(size.toVector2(), localOrigin.toVector2());
        List<Rectangle2D> rectangles = new LinkedList<>();
        split0(root, faces, worldMatrix, rectangles);
        List<BoxBody> bodies = new ArrayList<>(rectangles.size());
        for (Rectangle2D rectangle : rectangles) {
            Vector3 bodySize = new Vector3(rectangle.getWidth(), rectangle.getHeight(), this.boxBodyThickness);
            Vector3 bodyOrigin = new Vector3(rectangle.getPosition()).localToWorld(worldMatrix);
            bodies.add(new BoxBody(bodyOrigin, bodySize, this.normal, quaternion, this.physicsMaterial));
        }
        return bodies;
    }


    private void split0(Rectangle2D root, Set<Face> faces, Matrix4 worldMatrix, List<Rectangle2D> rectangles) {
        Rectangle2D[] subRects = root.split(areaThreshold);
        if (subRects != null) {
            for (int i = 0; i < subRects.length; i++) {
                Rectangle2D rect = subRects[i];
                int j = 0;
                if (isPointInPolygon(rect.getBottomLeft(), faces, worldMatrix))  j++;
                if (isPointInPolygon(rect.getUpperLeft(), faces, worldMatrix))   j++;
                if (isPointInPolygon(rect.getUpperRight(), faces, worldMatrix))  j++;
                if (isPointInPolygon(rect.getBottomRight(), faces, worldMatrix)) j++;
                if (j == 4) {
                    if (i > 0)
                        if (subRects[i - 1] != null) {
                            subRects[i - 1] = subRects[i - 1].merge(rect);
                            subRects[i] = null;
                        } else if (i == 3 && subRects[0] != null)
                            try {
                                subRects[0] = subRects[0].merge(rect);
                                subRects[i] = null;
                            } catch (IllegalArgumentException ignored) {
                            }
                } else {
                    subRects[i] = null;
                    if (j > 0)
                        split0(rect, faces, worldMatrix, rectangles);
                }
            }

            for (Rectangle2D rect : subRects)
                if (rect != null)
                    rectangles.add(rect);

            for (int i = 0; i < rectangles.size(); i++) {
                Rectangle2D rect = rectangles.get(i);
                int j = findFellow(rect, rectangles);
                while (j != -1) {
                    rect = rect.merge(rectangles.get(j));
                    rectangles.set(i, rect);
                    rectangles.remove(j);
                    if (j < i)
                        i--;
                    j = findFellow(rect, rectangles);
                }
            }
        }
    }

    private boolean isPointInPolygon(Vector2 point, Set<Face> faces, Matrix4 worldMatrix) {
        for (Face face : faces)
            if (face.containsPoint(point, worldMatrix, surface))
                return true;
        return false;
    }

    private int findFellow(Rectangle2D target, List<Rectangle2D> rectangles) {
        for (int i = 0; i < rectangles.size(); i++) {
            Rectangle2D potentialFellow = rectangles.get(i);
            if (target == potentialFellow)
                continue;
            if (target.hasAtLeastTwoCommonVerticesWith(potentialFellow))
                return i;
        }
        return -1;
    }
}
