package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.model.surface.Face;
import org.briarheart.doomthree.model.surface.Surface;
import org.briarheart.doomthree.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Roman Chigvintsev
 */
public class SurfaceSplitter {
    private final Surface surface;
    private final double areaThreshold;
    private final double boxBodyThickness;

    public SurfaceSplitter(Surface surface, double areaThreshold, double boxBodyThickness) {
        this.surface = surface;
        this.areaThreshold = areaThreshold;
        this.boxBodyThickness = boxBodyThickness;
    }

    public List<BoxBody> split(Set<Face> faces, Vector3 position, Quaternion quaternion, Vector3 size) {
        List<BoxBody> bodies = new ArrayList<>();
        Matrix4 worldMatrix = new Matrix4();
        worldMatrix.compose(position, quaternion);
        Vector3 localPosition = position.worldToLocal(worldMatrix);
        Rectangle rectangle = new Rectangle(size.toVector2(), localPosition.toVector2());
        SplitContext context = new SplitContext(faces, worldMatrix, quaternion, bodies);
        split0(rectangle, context);
        return bodies;
    }

    private void split0(Rectangle rectangle, SplitContext context) {
        Rectangle[] subRectangles = rectangle.split(areaThreshold);
        for (Rectangle r : subRectangles) {
            int i = 0;
            if (findFaceWithPoint(context.faces, r.getBottomLeft(),  context.worldMatrix)) i++;
            if (findFaceWithPoint(context.faces, r.getUpperLeft(),   context.worldMatrix)) i++;
            if (findFaceWithPoint(context.faces, r.getUpperRight(),  context.worldMatrix)) i++;
            if (findFaceWithPoint(context.faces, r.getBottomRight(), context.worldMatrix)) i++;
            if (i > 0) {
                if (i == 4) {
                    Vector3 size = new Vector3(r.getWidth(), r.getHeight(), boxBodyThickness);
                    Vector3 position = new Vector3(r.getPosition()).localToWorld(context.worldMatrix);
                    context.bodies.add(new BoxBody(size, position, context.quaternion));
                } else
                    split0(r, context);
            }
        }
    }

    private boolean findFaceWithPoint(Set<Face> faces, Vector2 point, Matrix4 worldMatrix) {
        for (Face face : faces)
            if (face.containsPoint(point, worldMatrix, surface))
                return true;
        return false;
    }

    private static class SplitContext {
        final Set<Face> faces;
        final Matrix4 worldMatrix;
        final Quaternion quaternion;
        final List<BoxBody> bodies;

        public SplitContext(Set<Face> faces, Matrix4 worldMatrix, Quaternion quaternion, List<BoxBody> bodies) {
            this.faces = faces;
            this.worldMatrix = worldMatrix;
            this.quaternion = quaternion;
            this.bodies = bodies;
        }
    }
}
