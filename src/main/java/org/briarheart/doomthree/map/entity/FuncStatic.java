package org.briarheart.doomthree.map.entity;

import org.apache.commons.lang3.StringUtils;
import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.entity.model.LwoModel;

import java.util.regex.Matcher;

/**
 * @author Roman Chigvintsev
 */
public class FuncStatic extends MovingEntity {
    private LwoModel lwoModel;

    public FuncStatic(String entityBody) {
        super(entityBody);
    }

    @Override
    public boolean visit(AbstractMap map, boolean warnIfFailed) {
        if (lwoModel == null)
            return super.visit(map, warnIfFailed);
        return lwoModel.visit(map, warnIfFailed);
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
    }
}
