package org.briarheart.doomthree.map;

import org.apache.commons.lang3.StringUtils;
import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.map.area.surface.Surface;
import org.briarheart.doomthree.map.entity.*;

import java.util.*;

/**
 * @author Roman Chigvintsev
 */
public abstract class AbstractMap {
    private final String name;
    private final String areaFilter;
    private final List<Md5ModelDef> md5ModelDefs = new LinkedList<>();
    private final List<Area> areas = new LinkedList<>();
    private final List<Light> lights = new ArrayList<>();
    private final List<TriggerRelay> triggers = new ArrayList<>();
    private final Meta meta;

    private InfoPlayerStart infoPlayerStart;
    private Skybox skybox;

    public AbstractMap(String name) {
        this(name, null);
    }

    public AbstractMap(String name, String areaFilter) {
        this.name = name;
        this.areaFilter = areaFilter;
        this.meta = new Meta(name);
    }

    public void preProcess() {
        // Override in subclasses
    }

    public void postProcess() {
        applyFilter();
        updateMapMeta();
    }

    public Meta getMeta() {
        return meta;
    }

    public String getName() {
        return name;
    }

    public String getAreaFilter() {
        return areaFilter;
    }

    public List<Md5ModelDef> getMd5ModelDefs() {
        return md5ModelDefs;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public List<TriggerRelay> getTriggers() {
        return triggers;
    }

    public InfoPlayerStart getInfoPlayerStart() {
        return infoPlayerStart;
    }

    public void setInfoPlayerStart(InfoPlayerStart infoPlayerStart) {
        this.infoPlayerStart = infoPlayerStart;
    }

    public Skybox getSkybox() {
        return skybox;
    }

    public void setSkybox(Skybox skybox) {
        this.skybox = skybox;
    }

    public boolean addEntity(Entity entity, boolean lastAttempt) {
        return entity.visit(this, lastAttempt);
    }

    public void addLight(Light light) {
        lights.add(light);
    }

    public Md5ModelDef newMd5ModelDef(String body) {
        return new Md5ModelDef(body);
    }

    public Area newArea(String body) {
        return new Area(this, body);
    }

    public Surface newSurface(Area area, String surfaceBody) {
        return new Surface(area, surfaceBody);
    }

    public String toJson() {
        StringBuilder json = new StringBuilder("{");

        if (infoPlayerStart != null)
            json.append(infoPlayerStart).append(",");

        if (skybox != null)
            json.append(skybox).append(",");

        if (!areas.isEmpty()) {
            json.append("\"areas\":[");
            for (int i = 0; i < areas.size(); i++) {
                Area area = areas.get(i);
                if (i > 0)
                    json.append(",");
                json.append(area);
            }
            json.append("]");
        }

        if (!lights.isEmpty()) {
            if (json.length() > 1)
                json.append(",");
            json.append("\"lights\":[");
            for (int i = 0; i < lights.size(); i++) {
                Light light = lights.get(i);
                if (i > 0)
                    json.append(",");
                json.append(light);
            }
            json.append("]");
        }

        if (!triggers.isEmpty()) {
            if (json.length() > 1)
                json.append(",");
            json.append("\"triggers\":[");
            for (int i = 0; i < triggers.size(); i++) {
                TriggerRelay trigger = triggers.get(i);
                if (i > 0)
                    json.append(",");
                json.append(trigger);
            }
            json.append("]");
        }

        return json.append("}").toString();
    }

    @Override
    public String toString() {
        return toJson();
    }

    protected void applyFilter() {
        if (!StringUtils.isEmpty(areaFilter)) {
            for (int i = 0; i < areas.size(); i++) {
                Area area = areas.get(i);
                if (area.getName().equals(areaFilter)) {
                    areas.clear();
                    areas.add(area);
                    return;
                }
            }
        }
    }

    protected void updateMapMeta() {
        for (Area area : areas)
            area.updateMapMeta(meta);
        if (skybox != null)
            skybox.updateMapMeta(meta);
    }

    public static class Meta {
        private final String name;
        private final Set<String> materials = new HashSet<>();
        private final Set<String> models = new HashSet<>();
        private final Set<String> animations = new HashSet<>();
        private final Set<String> sounds = new HashSet<>();

        public Meta(String name) {
            this.name = name;
        }

        public Set<String> getMaterials() {
            return materials;
        }

        public Set<String> getModels() {
            return models;
        }

        public Set<String> getAnimations() {
            return animations;
        }

        public Set<String> getSounds() {
            return sounds;
        }

        public String toJson() {
            StringBuilder json = new StringBuilder();
            json.append("{\"name\":\"").append(name).append("\"");
            if (!materials.isEmpty()) {
                json.append(",\"materials\":");
                writeSet(materials, json);
            }
            if (!models.isEmpty()) {
                json.append(",\"models\":");
                writeSet(models, json);
            }
            if (!animations.isEmpty()) {
                json.append(",\"animations\":");
                writeSet(animations, json);
            }
            if (!sounds.isEmpty()) {
                json.append(",\"sounds\":");
                writeSet(sounds, json);
            }
            return json + "}";
        }

        @Override
        public String toString() {
            return toJson();
        }

        private void writeSet(Set<String> set, StringBuilder json) {
            json.append("[");
            int i = 0;
            for (String element : set) {
                if (i++ > 0)
                    json.append(",");
                json.append("\"").append(element).append("\"");
            }
            json.append("]");
        }
    }
}
