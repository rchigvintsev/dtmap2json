package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.model.surface.Face;
import org.briarheart.doomthree.model.surface.Surface;
import org.briarheart.doomthree.util.Matrix4;
import org.briarheart.doomthree.util.Quaternion;
import org.briarheart.doomthree.util.Rectangle2D;
import org.briarheart.doomthree.util.Vector3;

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
        Matrix4 worldMatrix = new Matrix4();
        worldMatrix.compose(position, quaternion);
        Vector3 localPosition = position.worldToLocal(worldMatrix);
        Rectangle2D rectangle = new Rectangle2D(size.toVector2(), localPosition.toVector2());
        TileContainer root = new TileContainer(rectangle, areaThreshold, point -> {
            for (Face face : faces)
                if (face.containsPoint(point, worldMatrix, surface))
                    return true;
            return false;
        });
        BoxBodyCollector boxBodyCollector = new BoxBodyCollector(boxBodyThickness, worldMatrix, quaternion);
        root.apply(boxBodyCollector);
        return boxBodyCollector.getBoxBodies();
    }

}
