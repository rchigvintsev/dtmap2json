package org.briarheart.doomthree.model.surface.physics;

import org.briarheart.doomthree.model.surface.Face;
import org.briarheart.doomthree.model.surface.Surface;
import org.briarheart.doomthree.util.*;

import java.util.*;

/**
 * @author Roman Chigvintsev
 */
public class CollisionModel {
    private static final double AREA_DELTA_THRESHOLD = 1000.0;
    private static final double AREA_THRESHOLD = 100.0;
    private static final double DEFAULT_BOX_BODY_THICKNESS = 10.0;

    private List<BoxBody> bodies = new ArrayList<>();

    public CollisionModel(Surface surface, PhysicsMaterial physicsMaterial) {
        for (Map.Entry<Long, Set<Face>> entry : groupCoplanarFaces(surface).entrySet()) {
            Set<Face> faces = entry.getValue();
            if (faces.size() > 1) {
                double realArea = computeArea(faces, surface);
                if (realArea > AREA_THRESHOLD) {
                    Vector3 origin = computeOrigin(faces, surface);
                    Vector3 normal = getNormal(faces);
                    Quaternion quaternion = computeQuaternion(origin, normal);
                    Vector3 size = computeSize(faces, origin, quaternion, surface);

                    double bodyArea = size.x * size.y;
                    double areaDelta = bodyArea - realArea;

                    if (Math.abs(areaDelta) > AREA_DELTA_THRESHOLD) {
                        if (areaDelta < 0)
                            System.err.println("Part of surface \"" + surface.getName() + "\" of model \""
                                    + surface.getModel().getName() + "\" has area much larger than area of "
                                    + "physics body");
                        else {
                            SurfaceSplitter splitter = new SurfaceSplitter(surface, physicsMaterial, normal,
                                    AREA_THRESHOLD, DEFAULT_BOX_BODY_THICKNESS);
                            bodies.addAll(splitter.split(faces, origin, quaternion, size));
                        }
                    } else {
                        Vector3 bodySize = new Vector3(size.x, size.y, DEFAULT_BOX_BODY_THICKNESS);
                        bodies.add(new BoxBody(origin, bodySize, normal, quaternion, physicsMaterial));
                    }
                }
            }
        }
        reduceThicknessOfTightlyLocatedBoxBodies();
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

                    if (face.hasCommonEdgeWith(otherFace) && face.isCoplanar(otherFace)) {
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

    private void reduceThicknessOfTightlyLocatedBoxBodies() {
        for (int i = 0; i < bodies.size(); i++) {
            BoxBody body = bodies.get(i);
            Vector3 invertedNormal = body.normal.invert();
            for (int j = i + 1; j < bodies.size(); j++) {
                BoxBody otherBody = bodies.get(j);
                if (otherBody.normal.equals(invertedNormal)) {
                    Vector3 v1 = body.origin.multiply(body.normal);
                    Vector3 v2 = otherBody.origin.multiply(body.normal);
                    double distance = v1.distanceTo(v2);
                    if (distance < DEFAULT_BOX_BODY_THICKNESS && (distance < body.size.z || distance < otherBody.size.z)) {
                        Matrix4 worldMatrix = new Matrix4();
                        worldMatrix.compose(body.origin, body.quaternion);

                        Vector3 localOrigin = body.origin.worldToLocal(worldMatrix);
                        Rectangle2D rect = new Rectangle2D(body.size.toVector2(), localOrigin.toVector2());
                        BoundingBox bb = new BoundingBox();
                        bb.checkBoundaries(rect.getBottomLeft());
                        bb.checkBoundaries(rect.getUpperLeft());
                        bb.checkBoundaries(rect.getUpperRight());
                        bb.checkBoundaries(rect.getBottomRight());

                        Vector3 otherLocalOrigin = otherBody.origin.worldToLocal(worldMatrix);
                        Rectangle2D otherRect = new Rectangle2D(otherBody.size.toVector2(), otherLocalOrigin.toVector2());
                        BoundingBox otherBb = new BoundingBox();
                        otherBb.checkBoundaries(otherRect.getBottomLeft());
                        otherBb.checkBoundaries(otherRect.getUpperLeft());
                        otherBb.checkBoundaries(otherRect.getUpperRight());
                        otherBb.checkBoundaries(otherRect.getBottomRight());

                        if (bb.overlaps(otherBb)) {
                            if (body.size.z > distance) {
                                Vector3 newSize = new Vector3(body.size.x, body.size.y, distance);
                                bodies.set(i, new BoxBody(body.origin, newSize, body.normal, body.quaternion,
                                        body.material));
                            }

                            if (otherBody.size.z > distance) {
                                Vector3 newOtherSize = new Vector3(otherBody.size.x, otherBody.size.y, distance);
                                bodies.set(j, new BoxBody(otherBody.origin, newOtherSize, otherBody.normal,
                                        otherBody.quaternion, otherBody.material));
                            }
                        }
                    }
                }
            }
        }
    }

    private Vector3 getNormal(Set<Face> faces) {
        return faces.iterator().next().normal;
    }

    private Vector3 computeOrigin(Set<Face> faces, Surface surface) {
        BoundingBox boundingBox = new BoundingBox();
        for (Face face : faces) {
            boundingBox.checkBoundaries(surface.getVertices()[face.a].position);
            boundingBox.checkBoundaries(surface.getVertices()[face.b].position);
            boundingBox.checkBoundaries(surface.getVertices()[face.c].position);
        }
        double xPos = boundingBox.getMinX() + (boundingBox.getWidth() / 2.0);
        double yPos = boundingBox.getMinY() + (boundingBox.getHeight() / 2.0);
        double zPos = boundingBox.getMinZ() + (boundingBox.getDepth() / 2.0);
        return new Vector3(xPos, yPos, zPos);
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
        return new Vector3(width, height, DEFAULT_BOX_BODY_THICKNESS);
    }

    private double computeArea(Set<Face> faces, Surface surface) {
        double result = 0;
        for (Face face : faces)
            result += face.getArea(surface);
        return result;
    }
}
