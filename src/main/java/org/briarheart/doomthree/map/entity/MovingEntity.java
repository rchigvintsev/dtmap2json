package org.briarheart.doomthree.map.entity;

import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.map.area.surface.Surface;
import org.briarheart.doomthree.map.area.surface.physics.CollisionModel;
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
    public boolean visit(AbstractMap map, boolean warnIfFailed) {
        String modelName = getTargetAreaName();
        int areaIndex = findArea(map, modelName);
        if (areaIndex == -1) {
            if (warnIfFailed)
                System.err.println("Area with name \"" + modelName + "\" is not found");
            return false;
        }

        List<Area> areas = map.getAreas();
        Area area = areas.remove(areaIndex);
        for (Area otherArea : areas)
            if (otherArea.getBoundingBox().contains(position)) {
                for (Surface surface : area) {
                    surface.setPosition(position);
                    CollisionModel collisionModel = surface.getCollisionModel();
                    if (collisionModel != null)
                        collisionModel.getBody().setPosition(position);
                    otherArea.addSurface(surface);
                }
                return true;
            }

        if (warnIfFailed)
            System.err.println("Could not find area to accommodate another area with name \"" + area.getName() + "\"");
        return false;
    }

    protected abstract String getTargetAreaName();

    @Override
    protected void parse(String body) {
        position = parseOrigin(body);
    }

    private static int findArea(AbstractMap map, String modelName) {
        int areaIndex = -1;
        List<Area> areas = map.getAreas();
        for (int i = 0; i < areas.size(); i++) {
            Area area = areas.get(i);
            if (area.getName().equals(modelName)) {
                areaIndex = i;
                break;
            }
        }
        return areaIndex;
    }
}
