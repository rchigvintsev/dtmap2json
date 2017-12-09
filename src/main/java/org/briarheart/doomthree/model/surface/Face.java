package org.briarheart.doomthree.model.surface;

import org.briarheart.doomthree.Identifiable;
import org.briarheart.doomthree.util.Matrix4;
import org.briarheart.doomthree.util.Vector2;
import org.briarheart.doomthree.util.Vector3;

/**
 * @author Roman Chigvintsev
 */
public class Face implements Identifiable {
    private static final double EPSILON = 0.01;

    public final long id;
    public final int a;
    public final int b;
    public final int c;
    public Vector3 normal;
    private long groupId;

    public Face(long id, int[] a, Vector3 normal) {
        this(id, a[0], a[1], a[2], normal);
    }

    public Face(long id, int a, int b, int c, Vector3 normal) {
        this.id = id;
        this.a = a;
        this.b = b;
        this.c = c;
        this.normal = normal;
        setGroupId(id);
    }

    @Override
    public long getId() {
        return id;
    }

    public long getGroupId() {
        return groupId;
    }

    public void setGroupId(long groupId) {
        this.groupId = groupId;
    }

    public boolean hasCommonEdge(Face other) {
        int commonVerticesNumber = 0;
        if (other.containsVertex(a)) commonVerticesNumber++;
        if (other.containsVertex(b)) commonVerticesNumber++;
        if (other.containsVertex(c)) commonVerticesNumber++;
        return commonVerticesNumber == 2;
    }

    public boolean isCoplanar(Face other) {
        return normal.angleTo(other.normal) * (180 / Math.PI) < 0.01;
    }

    public boolean containsVertex(int v) {
        return v == a || v == b || v == c;
    }

    public boolean containsPoint(Vector2 point, Matrix4 worldMatrix, Surface surface) {
        // TODO: закэшировать вычисленные локальные точки
        Vector3 a = surface.getVertices()[this.a].position.worldToLocal(worldMatrix);
        Vector3 b = surface.getVertices()[this.b].position.worldToLocal(worldMatrix);
        Vector3 c = surface.getVertices()[this.c].position.worldToLocal(worldMatrix);

        double denominator = ((b.y - c.y) * (a.x - c.x) + (c.x - b.x) * (a.y - c.y));

        double x = ((b.y - c.y) * (point.x - c.x) + (c.x - b.x) * (point.y - c.y)) / denominator;
        double y = ((c.y - a.y) * (point.x - c.x) + (a.x - c.x) * (point.y - c.y)) / denominator;
        double z = 1.0 - x - y;

        return (0 - EPSILON <= x && x <= 1 + EPSILON)
                && (0 - EPSILON <= y && y <= 1 + EPSILON)
                && (0 - EPSILON <= z && z <= 1 + EPSILON);
    }

    public double getArea(Surface surface) {
        Vector3 a = surface.getVertices()[this.a].position;
        Vector3 b = surface.getVertices()[this.b].position;
        Vector3 c = surface.getVertices()[this.c].position;

        double ab = a.distanceTo(b);
        double bc = b.distanceTo(c);
        double ca = c.distanceTo(a);

        double hp = (ab + bc + ca) / 2;
        return Math.sqrt(hp * (hp - ab) * (hp - bc) * (hp - ca));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Face face = (Face) o;
        return id == face.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }

    @Override
    public String toString() {
        return toJson();
    }

    public String toJson() {
        return "[" + a + "," + b + "," + c + "]";
    }
}
