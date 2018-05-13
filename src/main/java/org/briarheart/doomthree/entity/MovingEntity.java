package org.briarheart.doomthree.entity;

import org.briarheart.doomthree.AbstractMap;
import org.briarheart.doomthree.model.Model;
import org.briarheart.doomthree.model.surface.Surface;
import org.briarheart.doomthree.model.surface.physics.CollisionModel;
import org.briarheart.doomthree.util.Vector3;

import java.util.List;

/**
 * @author Roman Chigvintsev
 */
public abstract class MovingEntity extends Entity {
    private Vector3 position;

    public MovingEntity(String entityBody) {
        super(entityBody);
    }

    @Override
    public void visit(AbstractMap map) {
        String modelName = getTargetModelName();
        int modelIndex = findModel(map, modelName);
        if (modelIndex == -1) {
            System.err.println("Model with name \"" + modelName + "\" is not found");
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

    protected abstract String getTargetModelName();

    @Override
    protected void parse(String body) {
        position = parseOrigin(body);
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
