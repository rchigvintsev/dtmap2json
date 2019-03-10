package org.briarheart.doomthree.map.entity.model;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoorModel extends LwoModel {
    public static final Pattern TIME_PATTERN = Pattern.compile("\"time\"\\s+\"([\\d.]+)\"");
    public static final Pattern MOVE_DIRECTION_PATTERN = Pattern.compile("\"movedir\"\\s+\"(\\d+)\"");

    private Float time;
    private Integer moveDirection;

    public DoorModel(String modelBody) {
        super(modelBody);
    }

    @Override
    protected void writeAttributes(StringBuilder json) {
        super.writeAttributes(json);
        if (time != null)
            json.append(",\"time\":").append(time);
        if (moveDirection != null)
            json.append(",\"moveDirection\":").append(moveDirection);
    }

    @Override
    protected void parse(String body) {
        super.parse(body);
        this.time = parseTime(body);
        this.moveDirection = parseMoveDirection(body);
    }

    private Float parseTime(String body) {
        Matcher matcher = TIME_PATTERN.matcher(body);
        if (matcher.find())
            return Float.parseFloat(matcher.group(1));
        return null;
    }

    private Integer parseMoveDirection(String body) {
        Matcher matcher = MOVE_DIRECTION_PATTERN.matcher(body);
        if (matcher.find())
            return Integer.parseInt(matcher.group(1));
        return null;
    }
}