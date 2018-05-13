package org.briarheart.doomthree.entity;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Chigvintsev
 */
public class EntityFactory {
    private static final Pattern CLASS_NAME_PATTERN = Pattern.compile("\"classname\"\\s+\"(\\w+)\"");

    private EntityFactory() {
        //no instance
    }

    public static Entity createEntity(String entityBody) {
        Matcher classNameMatcher = CLASS_NAME_PATTERN.matcher(entityBody);
        if (classNameMatcher.find()) {
            String className = classNameMatcher.group(1);
            switch (className) {
                case "worldspawn":
                    return new WorldSpawn(entityBody);
                case "info_player_start":
                    return new InfoPlayerStart(entityBody);
                case "light":
                    return new Light(entityBody);
                case "func_static":
                    return new FuncStatic(entityBody);
                case "moveable_base":
                    return new MoveableBase(entityBody);
            }
        }
        return null;
    }
}
