package org.briarheart.doomthree.map.entity.model;

import org.apache.commons.lang3.StringUtils;
import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.Md5ModelDef;
import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.util.Vector3;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Chigvintsev
 */
public class Md5Model extends AbstractModel {
    private static final Pattern START_ANIM_PATTERN = Pattern.compile("\"startanim\"\\s+\"(\\w+)\"");

    private final Md5ModelDef modelDef;

    private Vector3 position;
    private String startAnimation;

    public Md5Model(String modelBody, Md5ModelDef modelDef) {
        super(modelBody);
        this.modelDef = modelDef;
    }

    @Override
    public String getModel() {
        return modelDef.getMesh();
    }

    @Override
    public Map<String, String> getAnimations() {
        return modelDef.getAnimations();
    }

    @Override
    public boolean visit(AbstractMap map, boolean warnIfFailed) {
        if (!StringUtils.isEmpty(modelDef.getMesh())) {
            for (Area area : map.getAreas())
                if (area.getBoundingBox().contains(position)) {
                    area.addModel(this);
                    return true;
                }
            if (warnIfFailed)
                System.err.println("Could not find area to accommodate MD5 model with name \"" + getName() + "\"");
        }
        return false;
    }

    @Override
    public String toJson() {
        StringBuilder json = new StringBuilder("{\"model\":\"")
                .append(modelDef.getMesh()).append("\",")
                .append("\"position\":").append(position);
        if (!StringUtils.isEmpty(startAnimation))
            json.append(",\"startAnimation\":\"").append(startAnimation).append("\"");
        if (!modelDef.getAnimations().isEmpty()) {
            json.append(",\"animations\":[");
            int i = 0;
            for (String animation : modelDef.getAnimations().values()) {
                if (i++ > 0)
                    json.append(",");
                json.append("\"").append(animation).append("\"");
            }
            json.append("]");
        }
        return  json.append("}").toString();
    }

    @Override
    protected void parse(String body) {
        super.parse(body);
        Matcher startAnimMatcher = START_ANIM_PATTERN.matcher(body);
        if (startAnimMatcher.find())
            startAnimation = startAnimMatcher.group(1);
        position = parseOrigin(body);
    }
}
