package org.briarheart.doomthree.map.entity;

import org.apache.commons.lang3.StringUtils;
import org.briarheart.doomthree.map.AbstractMap;

import java.util.regex.Matcher;

public class FuncDoor extends Entity {
    private LwoModel lwoModel;

    public FuncDoor(String entityBody) {
        super(entityBody);
    }

    @Override
    public boolean visit(AbstractMap map, boolean warnIfFailed) {
        if (lwoModel != null)
            return lwoModel.visit(map, warnIfFailed);
        return true;
    }

    @Override
    public String toJson() {
        return "";
    }

    @Override
    protected void parse(String body) {
        String name = null;
        Matcher nameMatcher = NAME_PATTERN.matcher(body);
        if (nameMatcher.find())
            name = nameMatcher.group(1);
        else
            System.err.println("Failed to parse name of \"func_static\"");

        Matcher modelMatcher = MODEL_PATTERN.matcher(body);
        if (modelMatcher.find()) {
            String model = modelMatcher.group(1);
            if (!StringUtils.isEmpty(model) && LwoModel.isLwoModel(model))
                this.lwoModel = new LwoModel(body);
            else
                System.err.println("LWO model is not defined for \"func_door\" entity with name"
                        + (name == null ? "" : "\"" + name + "\""));
        }
    }
}
