package org.briarheart.doomthree.map.entity.model;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DoorModel extends LwoModel {
    public static final Pattern TEAM_PATTERN = Pattern.compile("\"team\"\\s+\"(\\w+)\"");
    public static final Pattern TIME_PATTERN = Pattern.compile("\"time\"\\s+\"([\\d.]+)\"");
    public static final Pattern MOVE_DIRECTION_PATTERN = Pattern.compile("\"movedir\"\\s+\"(\\d+)\"");
    public static final Pattern SOUND_OPEN_PATTERN = Pattern.compile("\"snd_open\"\\s+\"(\\w+)\"");
    public static final Pattern SOUND_CLOSE_PATTERN = Pattern.compile("\"snd_close\"\\s+\"(\\w+)\"");

    private String team;
    private Float time;
    private Integer moveDirection;
    private String soundOpen;
    private String soundClose;

    public DoorModel(String modelBody) {
        super(modelBody);
    }

    @Override
    public Map<String, String> getSounds() {
        if (soundOpen == null && soundClose == null)
            return super.getSounds();
        Map<String, String> sounds = new HashMap<>();
        if (soundOpen != null)
            sounds.put("open", soundOpen);
        if (soundClose != null)
            sounds.put("close", soundClose);
        return sounds;
    }

    @Override
    protected void writeAttributes(StringBuilder json) {
        super.writeAttributes(json);
        if (team != null)
            json.append(",\"team\":\"").append(team).append("\"");
        if (time != null)
            json.append(",\"time\":").append(time);
        if (moveDirection != null)
            json.append(",\"moveDirection\":").append(moveDirection);
        Map<String, String> sounds = getSounds();
        if (!sounds.isEmpty()) {
            json.append(",\"sounds\":{");
            int i = 0;
            for (Map.Entry<String, String> soundEntry : sounds.entrySet()) {
                if (i > 0)
                    json.append(",");
                json.append("\"").append(soundEntry.getKey()).append("\":");
                json.append("\"").append(soundEntry.getValue()).append("\"");
                i++;
            }
            json.append("}");
        }
    }

    @Override
    protected void parse(String body) {
        super.parse(body);
        this.team = parseTeam(body);
        this.time = parseTime(body);
        this.moveDirection = parseMoveDirection(body);
        this.soundOpen = parseSoundOpen(body);
        this.soundClose = parseSoundClose(body);
    }

    private String parseTeam(String body) {
        Matcher matcher = TEAM_PATTERN.matcher(body);
        if (matcher.find())
            return matcher.group(1);
        return null;
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

    private String parseSoundOpen(String body) {
        Matcher matcher = SOUND_OPEN_PATTERN.matcher(body);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }

    private String parseSoundClose(String body) {
        Matcher matcher = SOUND_CLOSE_PATTERN.matcher(body);
        if (matcher.find())
            return matcher.group(1);
        return null;
    }
}