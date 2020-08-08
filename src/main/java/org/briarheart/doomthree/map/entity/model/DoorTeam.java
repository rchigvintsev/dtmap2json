package org.briarheart.doomthree.map.entity.model;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Roman Chigvintsev
 */
public class DoorTeam {
    private static final Map<String, DoorTeam> doorTeams = new HashMap<>();

    private final String name;

    private boolean locked;

    private DoorTeam(String name) {
        this.name = name;
    }

    public static DoorTeam getInstance(String name) {
        DoorTeam team = doorTeams.get(name);
        if (team == null) {
            doorTeams.put(name, team = new DoorTeam(name));
        }
        return team;
    }

    public String getName() {
        return name;
    }

    public void setLocked(boolean locked) {
        // Change lock state only if new value is different from default (false)
        if (locked) {
            this.locked = true;
        }
    }

    public boolean isLocked() {
        return locked;
    }
}
