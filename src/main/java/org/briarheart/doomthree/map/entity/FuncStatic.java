package org.briarheart.doomthree.map.entity;

import org.apache.commons.lang3.StringUtils;
import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.map.area.surface.Surface;
import org.briarheart.doomthree.map.area.surface.physics.CollisionModel;
import org.briarheart.doomthree.map.entity.model.AbstractModel;
import org.briarheart.doomthree.map.entity.model.LwoModel;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Chigvintsev
 */
public class FuncStatic extends MovingEntity {
    public static final Pattern BIND_PATTERN = Pattern.compile("\"bind\"\\s+\"(\\w+)\"");

    private LwoModel lwoModel;
    private String bind;

    public FuncStatic(String entityBody) {
        super(entityBody);
    }

    @Override
    public boolean visit(AbstractMap map, boolean lastAttempt) {
        if (lwoModel == null) {
            if (bind == null)
                return super.visit(map, lastAttempt);

            // I am going to try to find model with which we should bind surfaces of area
            // with name == this.getTargetAreaName()

            int areaIndex = getTargetAreaIndex(map, lastAttempt);
            if (areaIndex == -1)
                return false;
            List<Area> areas = map.getAreas();
            Area targetArea = areas.get(areaIndex);

            AbstractModel model = findModel(map, bind);
            if (model == null) {
                if (lastAttempt)
                    System.err.println("Bind model with name \"" + bind + "\" is not found");
                return false;
            }

            for (Surface surface : targetArea) {
                surface.setPosition(getPosition());
                CollisionModel collisionModel = surface.getCollisionModel();
                if (collisionModel != null)
                    collisionModel.getBody().setPosition(getPosition());
                // TODO: Perhaps I should update bounding box of model's area here
                model.getBoundSurfaces().add(surface);
            }

            areas.remove(areaIndex);
            return true;
        }
        return lwoModel.visit(map, lastAttempt);
    }

    @Override
    public String toString() {
        return getName();
    }

    @Override
    public String toJson() {
        return "";
    }

    @Override
    protected String getTargetAreaName() {
        return getName();
    }

    @Override
    protected void parse(String body) {
        super.parse(body);
        Matcher modelMatcher = MODEL_PATTERN.matcher(body);
        if (modelMatcher.find()) {
            String model = modelMatcher.group(1);
            if (!StringUtils.isEmpty(model) && LwoModel.isLwoModel(model))
                this.lwoModel = new LwoModel(body);
        }
        this.bind = parseBind(body);
    }

    private String parseBind(String body) {
        Matcher matcher = BIND_PATTERN.matcher(body);
        return matcher.find() ? matcher.group(1) : null;
    }

    private static AbstractModel findModel(AbstractMap map, String modelName) {
        List<Area> areas = map.getAreas();
        for (Area area : areas)
            for (AbstractModel model : area.getModels())
                if (modelName.equals(model.getName()))
                    return model;
        return null;
    }
}
