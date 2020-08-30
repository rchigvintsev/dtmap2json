package org.briarheart.doomthree.map.area;

import org.apache.commons.lang3.Validate;
import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.area.surface.Surface;
import org.briarheart.doomthree.map.area.surface.physics.CollisionModel;
import org.briarheart.doomthree.map.entity.Light;
import org.briarheart.doomthree.map.entity.Skybox;
import org.briarheart.doomthree.map.entity.model.AbstractModel;
import org.briarheart.doomthree.map.material.Materials;
import org.briarheart.doomthree.util.BoundingBox;
import org.briarheart.doomthree.util.Vector3;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Chigvintsev
 */
public class Area implements Iterable<Surface> {
    private static final Pattern MODEL_HEADER_PATTERN = Pattern
            .compile("model \\{ /\\* name = \\*/ \"(\\w+)\" /\\* numSurfaces = \\*/ (\\d+)");
    private static final Pattern SURFACE_HEADER_PATTERN = Pattern
            .compile("/\\* surface \\d+ \\*/ \\{ \"([\\w/]+)\"");

    private final List<Surface> surfaces = new ArrayList<>();
    private final List<Light> lights = new ArrayList<>();
    private final List<AbstractModel> models = new ArrayList<>();

    private final AbstractMap map;

    private String name;
    private Vector3 position;
    private int numberOfSurfaces;
    private List<BoundingBox> boundingBoxes = Collections.emptyList();

    public Area(AbstractMap map, String areaBody) {
        this.map = map;
        parse(areaBody);
    }

    public String getName() {
        return name;
    }

    public Vector3 getPosition() {
        return position;
    }

    public void setPosition(Vector3 position) {
        this.position = position;
    }

    public List<BoundingBox> getBoundingBoxes() {
        return boundingBoxes;
    }

    public void setBoundingBoxes(List<BoundingBox> boundingBoxes) {
        this.boundingBoxes = boundingBoxes;
    }

    public List<AbstractModel> getModels() {
        return models;
    }

    public void addSurface(Surface surface) {
        this.surfaces.add(surface);
        this.numberOfSurfaces += 1;
    }

    public void addModel(AbstractModel model) {
        this.models.add(model);
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    public void updateMapMeta(AbstractMap.Meta meta) {
        for (Surface surface : surfaces)
            meta.getMaterials().add(surface.getMaterialName());
        for (AbstractModel model : models) {
            meta.getModels().add(model.getModel());
            for (Surface surface : model.getBoundSurfaces())
                meta.getMaterials().add(surface.getMaterialName());
            meta.getAnimations().addAll(model.getAnimations().values());
            meta.getSounds().addAll(model.getSounds().values());
        }
    }

    @Override
    public Iterator<Surface> iterator() {
        return surfaces.iterator();
    }

    @Override
    public String toString() {
        return toJson();
    }

    public String toJson() {
        StringBuilder json = new StringBuilder("{");
        json.append("\"name\":\"").append(name).append("\",");
        if (position != null)
            json.append("\"position\":").append(position).append(",");
        json.append("\"boundingBoxes\":[");
        for (int i = 0; i < boundingBoxes.size(); i++) {
            BoundingBox boundingBox = boundingBoxes.get(i);
            if (i > 0) {
                json.append(",");
            }
            json.append(boundingBox);
        }
        json.append("],");
        json.append("\"surfaces\":[");
        for (int i = 0; i < surfaces.size(); i++) {
            Surface surface = surfaces.get(i);
            if (i > 0)
                json.append(",");
            json.append(surface);
        }
        json.append("]");
        if (!lights.isEmpty()) {
            json.append(",\"lights\":[");
            for (int i = 0; i < lights.size(); i++) {
                Light light = lights.get(i);
                if (i > 0)
                    json.append(",");
                json.append(light);
            }
            json.append("]");
        }
        if (!models.isEmpty()) {
            json.append(",\"models\":[");
            for (int i = 0; i < models.size(); i++) {
                AbstractModel model = models.get(i);
                if (i > 0)
                    json.append(",");
                json.append(model);
            }
            json.append("]");
        }
        return json.append("}").toString();
    }

    public void copyTo(Area otherArea, Vector3 position) {
        for (Surface surface : surfaces) {
            surface.setPosition(position);
            CollisionModel collisionModel = surface.getCollisionModel();
            if (collisionModel != null)
                collisionModel.getBody().setPosition(position);
            otherArea.addSurface(surface);
        }

        for (AbstractModel model : models) {
            model.setPosition(position);
            otherArea.getModels().add(model);
        }

        for (Light light : lights) {
            light.setPosition(position);
            otherArea.addLight(light);
        }
    }

    public boolean containsPoint(Vector3 point) {
        for (BoundingBox boundingBox : boundingBoxes) {
            if (boundingBox.contains(point)) {
                return true;
            }
        }
        return false;
    }

    protected void parse(String areaBody) {
        Validate.notEmpty(areaBody, "Area body cannot be empty");

        Scanner scanner = new Scanner(areaBody);
        String firstLine = scanner.nextLine();
        Matcher matcher = MODEL_HEADER_PATTERN.matcher(firstLine);
        if (!matcher.find()) {
            System.err.println("First line of area body (" + firstLine + ") does not contain area name and " +
                    "number of surfaces");
            return;
        }

        this.name = matcher.group(1);
        this.numberOfSurfaces = Integer.parseInt(matcher.group(2));

        if (numberOfSurfaces > 0) {
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                if (line.startsWith("/* surface")) {
                    Surface surface = readNextSurface(scanner, line);
                    if (surface != null) {
                        if (Materials.isSkybox(surface.getMaterialName()))
                            new Skybox(surface.getMaterialName()).visit(this.map, true);
                        else {
                            this.surfaces.add(surface);
                        }
                    }
                }
            }
            checkNumberOfSurfaces();
        } else
            System.err.println("Area \"" + this.name + "\" does not have any surface");
    }

    private Surface readNextSurface(Scanner scanner, String header) {
        Matcher matcher = SURFACE_HEADER_PATTERN.matcher(header);
        if (matcher.find()) {
            StringBuilder surfaceBody = new StringBuilder(header);
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                surfaceBody.append("\n").append(line);
                if (line.equals("}"))
                    return map.newSurface(this, surfaceBody.toString());
            }
            System.err.println("Unexpected end of area body");
        } else
            System.err.println("Failed to parse surface material in area \"" + this.name + "\"");
        return null;
    }

    private void checkNumberOfSurfaces() {
        if (this.numberOfSurfaces != surfaces.size())
            System.err.println("Number of processed surfaces (" + this.surfaces.size() + ") does not equal " +
                    "number of surfaces declared in area header (" + this.numberOfSurfaces + ")");
    }
}
