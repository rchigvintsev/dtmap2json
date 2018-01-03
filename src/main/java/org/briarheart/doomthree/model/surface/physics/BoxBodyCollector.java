package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.util.Matrix4;
import org.briarheart.doomthree.util.Quaternion;
import org.briarheart.doomthree.util.Rectangle2D;
import org.briarheart.doomthree.util.Vector3;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Roman Chigvintsev
 */
public class BoxBodyCollector {
    private final List<BoxBody> boxBodies = new ArrayList<>();
    private final double boxBodyThickness;
    private final Matrix4 worldMatrix;
    private final Quaternion quaternion;

    public BoxBodyCollector(double boxBodyThickness, Matrix4 worldMatrix, Quaternion quaternion) {
        this.boxBodyThickness = boxBodyThickness;
        this.worldMatrix = worldMatrix;
        this.quaternion = quaternion;
    }

    public List<BoxBody> getBoxBodies() {
        return boxBodies;
    }

    public void visitTileContainer(TileContainer tileContainer) {
        for (Tile child : tileContainer)
            child.apply(this);
    }

    public void visitCompleteTile(CompleteTile completeTile) {
        Rectangle2D rectangle2D = completeTile.getRectangle2D();
        Vector3 size = new Vector3(rectangle2D.getWidth(), rectangle2D.getHeight(), this.boxBodyThickness);
        Vector3 position = new Vector3(rectangle2D.getPosition()).localToWorld(this.worldMatrix);
        boxBodies.add(new BoxBody(size, position, this.quaternion));
    }
}
