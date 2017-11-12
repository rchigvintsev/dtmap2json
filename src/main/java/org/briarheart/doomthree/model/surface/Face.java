package org.briarheart.doomthree.model.surface;

import org.briarheart.doomthree.Identifiable;
import org.briarheart.doomthree.util.Vector3;

/**
 * @author Roman Chigvintsev
 */
public class Face implements Identifiable {
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
