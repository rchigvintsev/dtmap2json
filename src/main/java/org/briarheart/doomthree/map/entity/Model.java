package org.briarheart.doomthree.map.entity;

import org.apache.commons.lang3.StringUtils;
import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.ModelDef;
import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.util.BoundingBox;
import org.briarheart.doomthree.util.Vector3;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Chigvintsev
 */
public class Model extends Entity {
    private static final Pattern NAME_PATTERN = Pattern.compile("\"name\"\\s+\"([\\w\\d_]+)\"");
    private static final Pattern START_ANIM_PATTERN = Pattern.compile("\"startanim\"\\s+\"([\\w\\d_]+)\"");

    private final ModelDef modelDef;

    private String name;
    private Vector3 position;
    private String startAnimation;

    public Model(String entityBody, ModelDef modelDef) {
        super(entityBody);
        this.modelDef = modelDef;
    }

    public String getMesh() {
        return modelDef.getMesh();
    }

    public Map<String, String> getAnimations() {
        return modelDef.getAnimations();
    }

    @Override
    public void visit(AbstractMap map) {
        if (!StringUtils.isEmpty(modelDef.getMesh())) {
            for (Area area : map.getAreas())
                if (area.getBoundingBox().contains(position)) {
                    area.addModel(this);
                    return;
                }
            System.err.println("Could not find area to accommodate model with name \"" + name + "\"");
        }
    }

    public BoundingBox getBoundingBox() {
        return null;
    }

    @Override
    public String toJson() {
        StringBuilder json = new StringBuilder("{\"name\":\"")
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
        Matcher nameMatcher = NAME_PATTERN.matcher(body);
        if (nameMatcher.find())
            name = nameMatcher.group(1);
        else
            System.err.println("Failed to parse model name");
        Matcher startAnimMatcher = START_ANIM_PATTERN.matcher(body);
        if (startAnimMatcher.find())
            startAnimation = startAnimMatcher.group(1);
        position = parseOrigin(body);
    }
}
