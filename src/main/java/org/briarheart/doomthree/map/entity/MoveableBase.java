package org.briarheart.doomthree.map.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Chigvintsev
 */
public class MoveableBase extends MovingEntity {
    private static final Pattern MODEL_PATTERN = Pattern.compile("\"model\"\\s+\"(\\w+)\"");

    private String model;

    public MoveableBase(String entityBody) {
        super(entityBody);
    }

    @Override
    public String toJson() {
        return "";
    }

    @Override
    protected String getTargetAreaName() {
        return model;
    }

    @Override
    protected void parse(String body) {
        super.parse(body);
        Matcher matcher = MODEL_PATTERN.matcher(body);
        if (matcher.find())
            this.model = matcher.group(1);
        else
            System.err.println("Failed to parse model of \"moveable_base\"");
    }
}
