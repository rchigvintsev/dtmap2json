package org.briarheart.doomthree.map.entity;

import org.briarheart.doomthree.map.AbstractMap;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TriggerRelay extends Entity {
    public static final Pattern DELAY_PATTERN = Pattern.compile("\"delay\"\\s+\"([\\d.]+)\"");
    public static final Pattern TARGET_PATTERN = Pattern.compile("\"target\\d?\"\\s+\"(\\w+)\"");

    private TriggerType type;
    private Float delay;
    private List<String> targets;

    public TriggerRelay(String entityBody) {
        super(entityBody);
    }

    @Override
    public boolean visit(AbstractMap map, boolean warnIfFailed) {
        if (type == null) {
            if (warnIfFailed)
                System.err.println("Trigger \"" + getName() + "\" has unsupported type");
            return false;
        }
        map.getTriggers().add(this);
        return true;
    }

    @Override
    public String toJson() {
        StringBuilder json = new StringBuilder("{")
                .append("\"name\":\"").append(getName()).append("\",")
                .append("\"type\":\"").append(type.name().toLowerCase()).append("\",");
        if (delay != null)
            json.append("\"delay\":").append(delay).append(",");
        json.append("\"targets\":[");
        for (int i = 0; i < targets.size(); i++) {
            String target = targets.get(i);
            if (i > 0)
                json.append(",");
            json.append("\"").append(target).append("\"");
        }
        return json.append("]}").toString();
    }

    @Override
    protected void parse(String body) {
        super.parse(body);
        this.delay = parseDelay(body);
        if (this.delay != null)
            this.type = TriggerType.DELAYED;
        this.targets = parseTargets(body, getName());
    }

    private Float parseDelay(String body) {
        Matcher matcher = DELAY_PATTERN.matcher(body);
        if (matcher.find())
            return Float.parseFloat(matcher.group(1));
        return null;
    }

    private List<String> parseTargets(String body, String name) {
        List<String> targets = new ArrayList<>();
        Matcher matcher = TARGET_PATTERN.matcher(body);
        while (matcher.find())
            targets.add(matcher.group(1));
        if (targets.isEmpty())
            System.err.println("Failed to parse targets of \"trigger_relay\" entity"
                    + (name != null ? " with name '\"" + name + "\"" : ""));
        return targets;
    }

    public enum TriggerType {DELAYED}
}