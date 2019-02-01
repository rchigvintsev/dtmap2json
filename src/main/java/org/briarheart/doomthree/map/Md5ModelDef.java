package org.briarheart.doomthree.map;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Roman Chigvintsev
 */
public class Md5ModelDef {
    private static final Pattern MODEL_DEF_HEADER_PATTERN = Pattern.compile("model (\\w+) \\{");

    private final Map<String, String> animations = new HashMap<>();

    private String name;
    private String mesh;

    public Md5ModelDef(String modelBody) {
        parse(modelBody);
    }

    public String getName() {
        return name;
    }

    public String getMesh() {
        return mesh;
    }

    public Map<String, String> getAnimations() {
        return animations;
    }

    protected void parse(String modelBody) {
        Validate.notEmpty(modelBody, "Model definition body cannot be empty");

        Scanner scanner = new Scanner(modelBody);
        String firstLine = scanner.nextLine();
        Matcher matcher = MODEL_DEF_HEADER_PATTERN.matcher(firstLine);
        if (!matcher.find()) {
            System.err.println("First line of model definition body (" + firstLine
                    + ") does not contain model definition name");
            return;
        }

        this.name = matcher.group(1);

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            if (line.startsWith("mesh"))
                mesh = line.substring("mesh".length()).trim();
            else if (line.startsWith("anim")) {
                String[] split = StringUtils.split(line, " \t,");
                if (split.length < 3)
                    System.err.println("Invalid animation declaration (" + line
                            + ") in model definition \"" + name + "\"");
                else {
                    String animationName = split[1];
                    String animation = split[2];
                    animations.put(animationName, animation);
                }
            }
        }
    }
}
