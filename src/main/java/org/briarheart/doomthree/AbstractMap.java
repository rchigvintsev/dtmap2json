package org.briarheart.doomthree;

import org.apache.commons.lang3.StringUtils;
import org.briarheart.doomthree.entity.Entity;
import org.briarheart.doomthree.entity.InfoPlayerStart;
import org.briarheart.doomthree.entity.Light;
import org.briarheart.doomthree.entity.Skybox;
import org.briarheart.doomthree.model.Model;
import org.briarheart.doomthree.model.surface.Surface;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * @author Roman Chigvintsev
 */
public abstract class AbstractMap {
    private final String name;
    private final String areaFilter;
    private final List<Model> models = new LinkedList<>();
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

    public List<Model> getModels() {
        return models;
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

    public Model newModel(String body) {
        return new Model(this, body);
    }

    public Surface newSurface(Model model, String surfaceBody) {
        return new Surface(model, surfaceBody);
    }

    public void addModel(Model model) {
        models.add(model);
    }

    public void applyFilter() {
        if (!StringUtils.isEmpty(areaFilter)) {
            for (int i = 0; i < models.size(); i++) {
                Model model = models.get(i);
                if (model.getName().equals(areaFilter)) {
                    models.clear();
                    models.add(model);
                    return;
                }
            }
        }
    }

    public void updateMapMeta() {
        for (Model model : models)
            model.updateMapMeta(meta);
        if (skybox != null)
            skybox.updateMapMeta(meta);
    }

    public String toJson() {
        StringBuilder json = new StringBuilder("{");

        if (infoPlayerStart != null)
            json.append(infoPlayerStart).append(",");

        if (skybox != null)
            json.append(skybox).append(",");

        if (!models.isEmpty()) {
            json.append("\"areas\":[");
            for (int i = 0; i < models.size(); i++) {
                Model model = models.get(i);
                if (i > 0)
                    json.append(",");
                json.append(model);
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
        private final List<String> materials = new ArrayList<>();

        public Meta(String name) {
            this.name = name;
        }

        public List<String> getMaterials() {
            return materials;
        }

        public String toJson() {
            StringBuilder json = new StringBuilder();
            json.append("{\"name\":\"").append(name).append("\"");
            if (!materials.isEmpty()) {
                json.append(",\"materials\":[");
                for (int i = 0; i < materials.size(); i++) {
                    if (i > 0)
                        json.append(",");
                    json.append("\"").append(materials.get(i)).append("\"");
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
