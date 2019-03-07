package org.briarheart.doomthree.map.entity;

import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.util.Vector3;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Chigvintsev
 */
public abstract class Entity {
    public static final Pattern NAME_PATTERN = Pattern.compile("\"name\"\\s+\"(\\w+)\"");
    public static final Pattern MODEL_PATTERN = Pattern.compile("\"model\"\\s+\"([\\w/.]+)\"");

    private static final Pattern ORIGIN_PATTERN = Pattern.compile("\"origin\"\\s+\"([0-9 -]+)\"");

    public Entity(String entityBody) {
        parse(entityBody);
    }

    /**
     * @return {@code true} if entity is successfully added to the map, {@code false} otherwise
     */
    public abstract boolean visit(AbstractMap map, boolean warnIfFailed);

    @Override
    public String toString() {
        return toJson();
    }

    public abstract String toJson();

    protected abstract void parse(String body);

    protected Vector3 parseOrigin(String s) {
        Matcher matcher = ORIGIN_PATTERN.matcher(s);
        if (matcher.find())
            return Vector3.fromString(matcher.group(1));
        System.err.println("Failed to parse entity origin");
        return new Vector3();
    }
}
