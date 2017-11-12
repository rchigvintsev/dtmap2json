package org.briarheart.doomthree;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.briarheart.doomthree.entity.Entity;
import org.briarheart.doomthree.entity.EntityFactory;
import org.briarheart.doomthree.model.Model;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * @author Roman Chigvintsev
 */
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length == 0) {
            System.err.println("Usage: java -jar <jar-name>.jar <map-name>");
            System.exit(1);
        }

        String mapName = args[0];
        String area = null;
        if (args.length > 1)
            area = args[1];

        Map map = new Map(mapName, area);

        readModels(map);
        readEntities(map);

        String mapJson = map.toJson();
        System.out.println(mapJson);
        Files.write(new File(mapName + ".json").toPath(), mapJson.getBytes());
        System.out.println(map.getMeta());
    }

    private static void readEntities(Map map) {
        try (Scanner scanner = new Scanner(new FileInputStream(map.getName() + ".map"))) {
            MutableInt lineNumber = new MutableInt();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber.increment();

                if (StringUtils.isEmpty(line) || line.startsWith("//"))
                    continue;

                if (line.equals("{")) {
                    int startedAt = lineNumber.intValue();
                    Entity entity = readNextEntity(scanner, lineNumber);
                    if (entity == null)
                        System.err.println("Unrecognized entity started at line " + startedAt);
                    else
                        map.addEntity(entity);
                } else if (line.equals("}"))
                    System.err.println("Unpaired closing curly brace is found at line " + lineNumber);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Entity readNextEntity(Scanner scanner, MutableInt lineNumber) {
        StringBuilder entityBody = new StringBuilder();
        Deque<Character> openedBraces = new LinkedList<>();

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();
            lineNumber.increment();

            if (StringUtils.isEmpty(line) || line.startsWith("//"))
                continue;

            if (line.equals("{"))
                openedBraces.push('{');
            else if (line.equals("}")) {
                if (openedBraces.isEmpty())
                    return EntityFactory.createEntity(entityBody.toString());
                openedBraces.pop();
            }

            entityBody.append(line).append("\n");
        }

        System.err.println("Unexpected end of file");
        return null;
    }

    private static void readModels(Map map) {
        try (Scanner scanner = new Scanner(new FileInputStream(map.getName() + ".proc"))) {
            MutableInt lineNumber = new MutableInt();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (StringUtils.isEmpty(line) || line.startsWith("//"))
                    continue;

                if (line.startsWith("model {")) {
                    int startedAt = lineNumber.intValue();
                    Model model = readNextModel(scanner, line, lineNumber);
                    if (model == null)
                        System.err.println("Unrecognized model started at line " + startedAt);
                    else
                        map.addModel(model);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Model readNextModel(Scanner scanner, String header, MutableInt lineNumber) {
        StringBuilder modelBody = new StringBuilder(header);
        Deque<Character> openedBraces = new LinkedList<>();
        openedBraces.push('{'); // Brace in header

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (StringUtils.isEmpty(line) || line.startsWith("//"))
                continue;

            if (line.equals("}")) {
                openedBraces.pop();
                if (openedBraces.isEmpty())
                    return new Model(modelBody.toString());
            } else if (line.contains("{"))
                openedBraces.push('{');

            modelBody.append("\n").append(line);
        }

        System.err.println("Unexpected end of file");
        return null;
    }
}
