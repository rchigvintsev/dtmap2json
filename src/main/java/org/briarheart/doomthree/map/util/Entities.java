package org.briarheart.doomthree.map.util;

import org.apache.commons.beanutils.BeanUtils;
import org.briarheart.doomthree.map.entity.Entity;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author Roman Chigvintsev
 */
public class Entities {
    private Entities() {
        //no instance
    }

    public static void overrideEntityProperties(Entity entity, Map<String, Object> properties) {
        if (properties != null) {
            try {
                BeanUtils.populate(entity, properties);
            } catch (IllegalAccessException | InvocationTargetException e) {
                System.err.println("Failed to override properties of entity \"" + entity.getName() + '"');
            }
        }
    }
}
