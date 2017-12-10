package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.model.surface.Face;
import org.briarheart.doomthree.model.surface.Surface;
import org.briarheart.doomthree.util.*;
import org.briarheart.doomthree.util.Rectangle.ExpansionDirection;

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
        Rectangle[] subRects = rectangle.split(areaThreshold);
        if (subRects == null)
            return;
        Rectangle[] notNeedToSplit = new Rectangle[4];
        Rectangle[] needToSplit = new Rectangle[4];
        for (int i = 0; i < subRects.length; i++) {
            Rectangle r = subRects[i];
            int j = 0;
            if (findFaceWithPoint(context.faces, r.getBottomLeft(), context.worldMatrix)) j++;
            if (findFaceWithPoint(context.faces, r.getUpperLeft(), context.worldMatrix)) j++;
            if (findFaceWithPoint(context.faces, r.getUpperRight(), context.worldMatrix)) j++;
            if (findFaceWithPoint(context.faces, r.getBottomRight(), context.worldMatrix)) j++;
            if (j > 0) {
                if (j == 4)
                    notNeedToSplit[i] = r;
                else
                    needToSplit[i] = r;
            }
        }

        if (notNeedToSplit[0] != null && notNeedToSplit[1] != null) {
            // Lower left and upper left can be merged
            notNeedToSplit[0] = notNeedToSplit[0].expand(ExpansionDirection.UP);
            notNeedToSplit[1] = null;
        } else if (notNeedToSplit[0] != null && notNeedToSplit[3] != null) {
            // Lower left and lower right can be merged
            notNeedToSplit[0] = notNeedToSplit[0].expand(ExpansionDirection.RIGHT);
            notNeedToSplit[3] = null;
        } else if (notNeedToSplit[2] != null && notNeedToSplit[3] != null) {
            // Upper right and lower right can be merged
            notNeedToSplit[2] = notNeedToSplit[2].expand(ExpansionDirection.DOWN);
            notNeedToSplit[3] = null;
        } else if (notNeedToSplit[1] != null && notNeedToSplit[2] != null) {
            // Upper left and upper right can be merged
            notNeedToSplit[2] = notNeedToSplit[2].expand(ExpansionDirection.LEFT);
            notNeedToSplit[1] = null;
        }

        for (Rectangle r : notNeedToSplit)
            if (r != null) {
                Vector3 size = new Vector3(r.getWidth(), r.getHeight(), boxBodyThickness);
                Vector3 position = new Vector3(r.getPosition()).localToWorld(context.worldMatrix);
                context.bodies.add(new BoxBody(size, position, context.quaternion));
            }

        for (Rectangle r : needToSplit)
            if (r != null)
                split0(r, context);
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
