package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.model.surface.Face;
import org.briarheart.doomthree.model.surface.Surface;
import org.briarheart.doomthree.util.BoundingBox;
import org.briarheart.doomthree.util.Matrix4;
import org.briarheart.doomthree.util.Quaternion;
import org.briarheart.doomthree.util.Vector3;

import java.util.*;

/**
 * @author Roman Chigvintsev
 */
public class CollisionModel {
    private static final double AREA_DELTA_THRESHOLD = 1000.0;
    private static final double AREA_THRESHOLD = 100.0;
    private static final double BOX_BODY_THICKNESS = 10.0;

    private List<BoxBody> bodies = new ArrayList<>();

    public CollisionModel(Surface surface) {
        Map<Long, Set<Face>> coplanarFaces = groupCoplanarFaces(surface);
        for (Long groupId : coplanarFaces.keySet()) {
            Set<Face> faces = coplanarFaces.get(groupId);
            if (faces.size() > 1) {
                double realArea = computeArea(faces, surface);
                if (realArea > AREA_THRESHOLD) {
                    Vector3 normal = getNormal(faces);

                    Vector3 position = computePosition(faces, normal, surface);
                    Quaternion quaternion = computeQuaternion(position, normal);
                    Vector3 size = computeSize(faces, position, quaternion, surface);

                    double bodyArea = size.x * size.y;
                    double areaDelta = bodyArea - realArea;

                    if (Math.abs(areaDelta) > AREA_DELTA_THRESHOLD) {
                        if (areaDelta < 0)
                            System.err.println("Part of surface \"" + surface.getName() + "\" of model \""
                                    + surface.getModel().getName() + "\" has area much larger than area of "
                                    + "physical body");
                        else {
                            SurfaceSplitter splitter = new SurfaceSplitter(surface, AREA_THRESHOLD, BOX_BODY_THICKNESS);
                            bodies.addAll(splitter.split(faces, position, quaternion, size));
                        }
                    } else
                        bodies.add(new BoxBody(size, position, quaternion));
                }
            }
        }
    }

    public String toJson() {
        StringBuilder json = new StringBuilder();
        json.append("{\"bodies\":[");
        for (int i = 0; i < bodies.size(); i++) {
            if (i > 0)
                json.append(",");
            json.append(bodies.get(i).toJson());
        }
        return json.append("]}").toString();
    }

    @Override
    public String toString() {
        return toJson();
    }

    private Map<Long, Set<Face>> groupCoplanarFaces(Surface surface) {
        Map<Long, Set<Face>> result = new HashMap<>();
        Map<Long, Boolean> visited = new HashMap<>();

        for (Face face : surface.getFaces()) {
            if (visited.getOrDefault(face.getId(), false))
                continue;
            Deque<Face> path = new LinkedList<>();

            while (face != null) {
                visited.put(face.getId(), true);
                boolean found = false;
                for (Face otherFace : surface.getFaces()) {
                    if (face == otherFace || visited.getOrDefault(otherFace.getId(), false))
                        continue;

                    if (face.hasCommonEdge(otherFace) && face.isCoplanar(otherFace)) {
                        Set<Face> faceGroup = result.get(face.getGroupId());
                        Set<Face> otherFaceGroup = result.get(otherFace.getGroupId());

                        if (faceGroup == null && otherFaceGroup == null) {
                            faceGroup = new HashSet<>();
                            faceGroup.add(face);
                            faceGroup.add(otherFace);
                            result.put(face.getGroupId(), faceGroup);
                            otherFace.setGroupId(face.getGroupId());
                        } else {
                            if (faceGroup == null) {
                                otherFaceGroup.add(face);
                                face.setGroupId(otherFace.getGroupId());
                            } else if (otherFaceGroup == null) {
                                faceGroup.add(otherFace);
                                otherFace.setGroupId(face.getGroupId());
                            } else {
                                faceGroup.addAll(otherFaceGroup);
                                result.remove(otherFace.getGroupId());
                                for (Face f : otherFaceGroup)
                                    f.setGroupId(face.getGroupId());
                            }
                        }

                        path.push(face);
                        face = otherFace;
                        found = true;
                        break;
                    }
                }

                if (!found)
                    face = path.poll();
            }
        }
        return result;
    }

    private Vector3 getNormal(Set<Face> faces) {
        return faces.iterator().next().normal;
    }

    private Vector3 computePosition(Set<Face> faces, Vector3 normal, Surface surface) {
        BoundingBox boundingBox = new BoundingBox();
        for (Face face : faces) {
            boundingBox.checkBoundaries(surface.getVertices()[face.a].position);
            boundingBox.checkBoundaries(surface.getVertices()[face.b].position);
            boundingBox.checkBoundaries(surface.getVertices()[face.c].position);
        }
        double xPos = boundingBox.getMinX() + (boundingBox.getWidth() / 2.0);
        double yPos = boundingBox.getMinY() + (boundingBox.getHeight() / 2.0);
        double zPos = boundingBox.getMinZ() + (boundingBox.getDepth() / 2.0);
        Vector3 position = new Vector3(xPos, yPos, zPos);
        return position.add(new Vector3(normal.x, normal.y, normal.z).invert().multiplyScalar(5));
    }

    private Quaternion computeQuaternion(Vector3 position, Vector3 normal) {
        Vector3 v = position.add(normal);
        Matrix4 rotationMatrix = new Matrix4().lookAt(v, position, new Vector3(0, 1, 0));
        return new Quaternion().setFromRotationMatrix(rotationMatrix);
    }

    private Vector3 computeSize(Set<Face> faces, Vector3 position, Quaternion quaternion, Surface surface) {
        Matrix4 worldMatrix = new Matrix4();
        worldMatrix.compose(position, quaternion);

        BoundingBox boundingBox = new BoundingBox();
        for (Face face : faces) {
            boundingBox.checkBoundaries(surface.getVertices()[face.a].position.worldToLocal(worldMatrix));
            boundingBox.checkBoundaries(surface.getVertices()[face.b].position.worldToLocal(worldMatrix));
            boundingBox.checkBoundaries(surface.getVertices()[face.c].position.worldToLocal(worldMatrix));
        }

        double width = boundingBox.getWidth();
        double height = boundingBox.getHeight();
        return new Vector3(width, height, BOX_BODY_THICKNESS);
    }

    private double computeArea(Set<Face> faces, Surface surface) {
        double result = 0;
        for (Face face : faces)
            result += face.getArea(surface);
        return result;
    }
}
