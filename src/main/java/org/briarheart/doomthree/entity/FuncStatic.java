package org.briarheart.doomthree.entity;

import org.briarheart.doomthree.AbstractMap;
import org.briarheart.doomthree.model.Model;
import org.briarheart.doomthree.model.surface.Surface;
import org.briarheart.doomthree.model.surface.physics.CollisionModel;
import org.briarheart.doomthree.util.Vector3;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Chigvintsev
 */
public class FuncStatic extends Entity {
    private static final Pattern NAME_PATTERN = Pattern.compile("\"name\"\\s+\"([\\w\\d_]+)\"");

    private String name;
    private Vector3 position;

    public FuncStatic(String entityBody) {
        super(entityBody);
    }

    @Override
    public void visit(AbstractMap map) {
        int modelIndex = findModel(map, name);
        if (modelIndex == -1) {
            System.err.println("Static function \"" + name + "\" does not have corresponding model");
            return;
        }

        List<Model> models = map.getModels();
        Model model = models.remove(modelIndex);
        for (Model otherModel : models)
            if (otherModel.getBoundingBox().contains(position)) {
                for (Surface surface : model) {
                    surface.setPosition(position);
                    CollisionModel collisionModel = surface.getCollisionModel();
                    if (collisionModel != null)
                        collisionModel.getBody().setPosition(position);
                    otherModel.addSurface(surface);
                }
                return;
            }

        System.err.println("Could not find model to accommodate another model with name \"" + model.getName() + "\"");
    }

    @Override
    public String toJson() {
        return "";
    }

    @Override
    protected void parse(String body) {
        position = parseOrigin(body);
        Matcher matcher = NAME_PATTERN.matcher(body);
        if (matcher.find())
            this.name = matcher.group(1);
        else
            System.err.println("Failed to parse name of \"func_static\"");
    }

    private static int findModel(AbstractMap map, String modelName) {
        int modelIndex = -1;
        List<Model> models = map.getModels();
        for (int i = 0; i < models.size(); i++) {
            Model model = models.get(i);
            if (model.getName().equals(modelName)) {
                modelIndex = i;
                break;
            }
        }
        return modelIndex;
    }
}
