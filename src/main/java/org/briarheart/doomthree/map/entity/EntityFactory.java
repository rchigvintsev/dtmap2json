package org.briarheart.doomthree.map.entity;

import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.Md5ModelDef;

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

    public static Entity createEntity(String entityBody, AbstractMap map) {
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
                case "func_door":
                    return new FuncDoor(entityBody);
                default:
                    Md5ModelDef modelDef = findMd5ModelDef(map, className);
                    if (modelDef != null)
                        return new Md5Model(entityBody, modelDef);
            }
        }
        return null;
    }

    private static Md5ModelDef findMd5ModelDef(AbstractMap map, String name) {
        for (Md5ModelDef modelDef : map.getMd5ModelDefs())
            if (modelDef.getName().equals(name))
                return modelDef;
        return null;
    }
}
