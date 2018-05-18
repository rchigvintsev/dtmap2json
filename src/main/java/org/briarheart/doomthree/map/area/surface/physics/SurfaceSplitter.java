package org.briarheart.doomthree.map.area.surface.physics;

import org.briarheart.doomthree.map.area.surface.Face;
import org.briarheart.doomthree.map.area.surface.Surface;
import org.briarheart.doomthree.map.area.surface.physics.body.BoxBody;
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

    public BoxBody split(Set<Face> faces, Vector3 origin, Quaternion quaternion, Vector3 size) {
        Matrix4 worldMatrix = new Matrix4();
        worldMatrix.compose(origin, quaternion);
        Vector3 localOrigin = origin.worldToLocal(worldMatrix);
        List<Rectangle2D> roots = createRoots(size, localOrigin);
        List<Rectangle2D> rectangles = new LinkedList<>();
        for (Rectangle2D root : roots)
            split0(root, faces, worldMatrix, rectangles);
        BoxBody body = new BoxBody(origin, this.normal, this.physicsMaterial);
        for (Rectangle2D rectangle : rectangles) {
            Vector3 shapeSize = new Vector3(rectangle.width, rectangle.height, this.boxBodyThickness);
            Vector3 shapeOffset = new Vector3(rectangle.position)
                    .localToWorld(worldMatrix)
                    .sub(origin)
                    .add(this.normal.invert().multiplyScalar(shapeSize.z / 2.0));
            body.getShapes().add(new BoxBody.Shape(shapeSize, shapeOffset, quaternion));
        }
        return body;
    }

    /**
     * Divides rectangles with large difference between lengths of the sides into square-like parts.
     */
    private List<Rectangle2D> createRoots(Vector3 size, Vector3 origin) {
        List<Rectangle2D> result = new ArrayList<>();

        double width = size.x, height = size.y;
        double a = Math.max(width, height), b = Math.min(width, height);
        double p = 0.0;

        int c = 0;
        if (width != height) {
            p = width > height ? origin.x : origin.y;
            double ratio = b / a;
            while (ratio < 0.5) {
                a /= 2.0;
                p -= a / 2;
                ratio = b / a;
                c++;
            }
        }

        if (c == 0)
            result.add(new Rectangle2D(width, height, origin.toVector2()));
        else {
            int length = (int) Math.pow(2, c);
            for (int i = 0; i < length; i++) {
                if (width > height) {
                    Vector2 position = new Vector2(p + a * i, origin.y);
                    result.add(new Rectangle2D(a, b, position));
                } else {
                    Vector2 position = new Vector2(origin.x, p + a * i);
                    result.add(new Rectangle2D(b, a, position));
                }
            }
        }

        return result;
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
