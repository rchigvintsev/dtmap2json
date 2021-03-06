package org.briarheart.doomthree.map.entity;

import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.area.Area;
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
    public boolean visit(AbstractMap map, boolean lastAttempt) {
        int areaIndex = getTargetAreaIndex(map, lastAttempt);
        if (areaIndex == -1) {
            return false;
        }

        List<Area> areas = map.getAreas();
        Area area = areas.get(areaIndex);
        Area targetArea = null;
        for (Area otherArea : areas) {
            if (otherArea.containsPoint(position)) {
                targetArea = otherArea;
                if (map.matchesAreaFilter(otherArea.getName())) {
                    break;
                }
            }
        }

        if (targetArea != null) {
            beforeAreaCopy(area, targetArea);
            area.copyTo(targetArea, position);
            afterAreaCopy(area, targetArea);
            areas.remove(areaIndex);
            return true;
        }

        if (lastAttempt) {
            System.err.println("Could not find area to accommodate another area with name \"" + area.getName() + "\"");
        }
        return false;
    }

    protected abstract String getTargetAreaName();

    protected Vector3 getPosition() {
        return position;
    }

    protected int getTargetAreaIndex(AbstractMap map, boolean lastAttempt) {
        String areaName = getTargetAreaName();
        int areaIndex = findArea(map, areaName);
        if (areaIndex == -1 && lastAttempt) {
            System.err.println("Area with name \"" + areaName + "\" is not found");
        }
        return areaIndex;
    }

    @Override
    protected void parse(String body) {
        super.parse(body);
        position = parseOrigin(body);
    }

    protected static int findArea(AbstractMap map, String modelName) {
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

    protected void beforeAreaCopy(Area from, Area to) {
    }

    protected void afterAreaCopy(Area from, Area to) {
    }
}
