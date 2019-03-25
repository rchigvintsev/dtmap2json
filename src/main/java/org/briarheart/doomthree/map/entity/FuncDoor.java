package org.briarheart.doomthree.map.entity;

import org.apache.commons.lang3.StringUtils;
import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.entity.model.DoorModel;
import org.briarheart.doomthree.map.entity.model.LwoModel;

import java.util.regex.Matcher;

public class FuncDoor extends Entity {
    private DoorModel doorModel;

    public FuncDoor(String entityBody) {
        super(entityBody);
    }

    @Override
    public boolean visit(AbstractMap map, boolean lastAttempt) {
        if (doorModel != null)
            return doorModel.visit(map, lastAttempt);
        return true;
    }

    @Override
    public String toJson() {
        return "";
    }

    @Override
    protected void parse(String body) {
        super.parse(body);
        String model = parseModel(body);
        if (!StringUtils.isEmpty(model) && LwoModel.isLwoModel(model))
            this.doorModel = new DoorModel(body);
        else
            System.err.println("LWO model is not defined for \"func_door\" entity"
                    + (getName() == null ? "" : " with name \"" + getName() + "\""));
    }

    private String parseModel(String body) {
        Matcher matcher = MODEL_PATTERN.matcher(body);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }
}