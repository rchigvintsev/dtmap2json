package org.briarheart.doomthree.map;

import org.apache.commons.lang3.StringUtils;
import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.map.area.surface.Surface;
import org.briarheart.doomthree.map.entity.Entity;
import org.briarheart.doomthree.map.entity.InfoPlayerStart;
import org.briarheart.doomthree.map.entity.Light;
import org.briarheart.doomthree.map.entity.Skybox;

import java.util.*;

/**
 * @author Roman Chigvintsev
 */
public abstract class AbstractMap {
    private final String name;
    private final String areaFilter;
    private final List<ModelDef> modelDefs = new LinkedList<>();
    private final List<Area> areas = new LinkedList<>();
    private final List<Light> lights = new ArrayList<>();
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

    public Meta getMeta() {
        return meta;
    }

    public String getName() {
        return name;
    }

    public String getAreaFilter() {
        return areaFilter;
    }

    public List<ModelDef> getModelDefs() {
        return modelDefs;
    }

    public List<Area> getAreas() {
        return areas;
    }

    public List<Light> getLights() {
        return lights;
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

    public void addEntity(Entity entity) {
        entity.visit(this);
    }

    public ModelDef newModelDef(String body) {
        return new ModelDef(body);
    }

    public Area newArea(String body) {
        return new Area(this, body);
    }

    public Surface newSurface(Area area, String surfaceBody) {
        return new Surface(area, surfaceBody);
    }

    public void applyFilter() {
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

    public void updateMapMeta() {
        for (Area area : areas)
            area.updateMapMeta(meta);
        if (skybox != null)
            skybox.updateMapMeta(meta);
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

        return json.append("}").toString();
    }

    @Override
    public String toString() {
        return toJson();
    }

    public static class Meta {
        private final String name;
        private final Set<String> materials = new HashSet<>();
        private final Set<String> models = new HashSet<>();
        private final Set<String> animations = new HashSet<>();

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

        public String toJson() {
            StringBuilder json = new StringBuilder();
            json.append("{\"name\":\"").append(name).append("\"");
            if (!materials.isEmpty()) {
                json.append(",\"materials\":[");
                int i = 0;
                for (String material : materials) {
                    if (i++ > 0)
                        json.append(",");
                    json.append("\"").append(material).append("\"");
                }
                json.append("]");
            }
            if (!models.isEmpty()) {
                json.append(",\"models\":[");
                int i = 0;
                for (String model : models) {
                    if (i++ > 0)
                        json.append(",");
                    json.append("\"").append(model).append("\"");
                }
                json.append("]");
            }
            if (!animations.isEmpty()) {
                json.append(",\"animations\":[");
                int i = 0;
                for (String animation : animations) {
                    if (i++ > 0)
                        json.append(",");
                    json.append("\"").append(animation).append("\"");
                }
                json.append("]");
            }
            return json + "}";
        }

        @Override
        public String toString() {
            return toJson();
        }
    }
}
