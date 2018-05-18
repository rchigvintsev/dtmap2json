package org.briarheart.doomthree.map.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Chigvintsev
 */
public class FuncStatic extends MovingEntity {
    private static final Pattern NAME_PATTERN = Pattern.compile("\"name\"\\s+\"([\\w\\d_]+)\"");

    private String name;

    public FuncStatic(String entityBody) {
        super(entityBody);
    }

    @Override
    public String toJson() {
        return "";
    }

    @Override
    protected String getTargetAreaName() {
        return name;
    }

    @Override
    protected void parse(String body) {
        super.parse(body);
        Matcher matcher = NAME_PATTERN.matcher(body);
        if (matcher.find())
            this.name = matcher.group(1);
        else
            System.err.println("Failed to parse name of \"func_static\"");
    }
}
