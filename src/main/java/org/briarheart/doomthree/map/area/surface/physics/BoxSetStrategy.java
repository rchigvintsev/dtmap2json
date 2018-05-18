package org.briarheart.doomthree.map.area.surface.physics;

import org.briarheart.doomthree.map.area.surface.Face;
import org.briarheart.doomthree.map.area.surface.Surface;
import org.briarheart.doomthree.map.area.surface.physics.body.Body;
import org.briarheart.doomthree.map.area.surface.physics.body.BoxBody;
import org.briarheart.doomthree.util.*;

import java.util.*;

/**
 * @author Roman Chigvintsev
 */
public class BoxSetStrategy implements CollisionModelBuildingStrategy {
    private static final double AREA_DELTA_THRESHOLD = 5;
    private static final double AREA_THRESHOLD = 100.0;
    private static final double DEFAULT_BOX_BODY_THICKNESS = 10.0;

    @Override
    public Body createBody(Surface surface, PhysicsMaterial physicsMaterial) {
        List<BoxBody> bodies = new ArrayList<>();
        Map<Long, Set<Face>> coplanarFaces = groupCoplanarFaces(surface);
        for (Map.Entry<Long, Set<Face>> entry : coplanarFaces.entrySet()) {
            Set<Face> faces = entry.getValue();
            if (faces.size() > 1) {
                double realArea = computeArea(faces, surface);
                if (realArea > AREA_THRESHOLD) {
                    Vector3 origin = computeOrigin(faces, surface);
                    Vector3 normal = computeNormal(faces);
                    Quaternion quaternion = computeQuaternion(origin, normal);
                    Vector3 size = computeSize(faces, origin, quaternion, surface);

                    double bodyArea = size.x * size.y;
                    double areaDelta = (bodyArea - realArea) / (realArea / 100.0);

                    if (Math.abs(areaDelta) > AREA_DELTA_THRESHOLD) {
                        if (areaDelta < 0)
                            System.err.println("Part of surface \"" + surface.getName() + "\" of area \""
                                    + surface.getArea().getName() + "\" has area much larger than area of "
                                    + "physics body");
                        else {
                            SurfaceSplitter splitter = new SurfaceSplitter(surface, physicsMaterial, normal,
                                    AREA_THRESHOLD, DEFAULT_BOX_BODY_THICKNESS);
                            BoxBody body = splitter.split(faces, origin, quaternion, size);
                            if (!body.getShapes().isEmpty())
                                bodies.add(body);
                        }
                    } else {
                        BoxBody body = new BoxBody(origin, normal, physicsMaterial);
                        Vector3 shapeSize = new Vector3(size.x, size.y, DEFAULT_BOX_BODY_THICKNESS);
                        Vector3 shapeOffset = new Vector3()
                                .add(normal.invert().multiplyScalar(shapeSize.z / 2.0));
                        body.getShapes().add(new BoxBody.Shape(shapeSize, shapeOffset, quaternion));
                        bodies.add(body);
                    }
                }
            }
        }
        reduceDepthOfTightlyLocatedShapes(bodies);

        if (bodies.isEmpty())
            return null;
        BoxBody firstBody = bodies.get(0);
        for (int i = 1; i < bodies.size(); i++) {
            BoxBody body = bodies.get(i);
            Vector3 positionDelta = body.getPosition().sub(firstBody.getPosition());
            for (BoxBody.Shape shape : body.getShapes()) {
                Vector3 newOffset = shape.offset.add(positionDelta);
                firstBody.getShapes().add(new BoxBody.Shape(shape.size, newOffset, shape.quaternion));
            }
        }
        return firstBody;
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

    private void reduceDepthOfTightlyLocatedShapes(List<BoxBody> bodies) {
        for (int bi = 0; bi < bodies.size(); bi++) {
            BoxBody body = bodies.get(bi);
            Vector3 invertedNormal = body.normal.invert();
            for (int bj = bi + 1; bj < bodies.size(); bj++) {
                BoxBody otherBody = bodies.get(bj);
                if (!otherBody.normal.equals(invertedNormal))
                    continue;

                double distance = computeDistanceBetweenBodies(body, otherBody);
                if (distance >= DEFAULT_BOX_BODY_THICKNESS)
                    continue;

                for (int si = 0; si < body.getShapes().size(); si++) {
                    BoxBody.Shape shape = body.getShapes().get(si);
                    if (shape.size.z <= distance)
                        continue;

                    Matrix4 worldMatrix = new Matrix4();
                    worldMatrix.compose(body.getPosition(), shape.quaternion);

                    Vector3 localOrigin = body.getPosition().worldToLocal(worldMatrix);
                    BoundingBox bb = createBoundingBox(shape.size.toVector2(), localOrigin.toVector2());

                    for (int sj = 0; sj < otherBody.getShapes().size(); sj++) {
                        BoxBody.Shape otherShape = otherBody.getShapes().get(sj);
                        if (otherShape.size.z <= distance)
                            continue;

                        Vector3 otherLocalOrigin = otherBody.getPosition().worldToLocal(worldMatrix);
                        BoundingBox otherBb = createBoundingBox(otherShape.size.toVector2(), otherLocalOrigin.toVector2());

                        if (bb.overlaps(otherBb)) {
                            body.getShapes().set(si, reduceShapeDepth(shape, body.normal, distance));
                            otherBody.getShapes().set(sj, reduceShapeDepth(otherShape, otherBody.normal, distance));
                        }
                    }
                }
            }
        }
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

    /**
     * Computes average normal for faces set.
     */
    private Vector3 computeNormal(Set<Face> faces) {
        double x = 0.0, y = 0.0, z = 0.0;
        for (Face face : faces) {
            x += face.normal.x;
            y += face.normal.y;
            z += face.normal.z;
        }
        return new Vector3(x / faces.size(), y / faces.size(), z / faces.size());
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

    private double computeDistanceBetweenBodies(BoxBody body1, BoxBody body2) {
        Vector3 v1 = body1.getPosition().multiply(body1.normal);
        Vector3 v2 = body2.getPosition().multiply(body1.normal);
        return v1.distanceTo(v2);
    }

    private BoundingBox createBoundingBox(Vector2 size, Vector2 position) {
        Rectangle2D rect = new Rectangle2D(size, position);
        BoundingBox bb = new BoundingBox();
        bb.checkBoundaries(rect.getBottomLeft());
        bb.checkBoundaries(rect.getUpperLeft());
        bb.checkBoundaries(rect.getUpperRight());
        bb.checkBoundaries(rect.getBottomRight());
        return bb;
    }

    private BoxBody.Shape reduceShapeDepth(BoxBody.Shape shape, Vector3 normal, double newDepth) {
        Vector3 newSize = new Vector3(shape.size.x, shape.size.y, newDepth);
        Vector3 invertedNormal = normal.invert();
        Vector3 newOffset = shape.offset
                .sub(invertedNormal.multiplyScalar(shape.size.z / 2.0))
                .add(invertedNormal.multiplyScalar(newSize.z / 2.0));
        return new BoxBody.Shape(newSize, newOffset, shape.quaternion);
    }
}
