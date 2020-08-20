package org.briarheart.doomthree;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableInt;
import org.briarheart.doomthree.map.AbstractMap;
import org.briarheart.doomthree.map.MapFactory;
import org.briarheart.doomthree.map.Md5ModelDef;
import org.briarheart.doomthree.map.area.Area;
import org.briarheart.doomthree.map.entity.Entity;
import org.briarheart.doomthree.map.entity.EntityFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.util.*;

/**
 * @author Roman Chigvintsev
 */
public class Main {
    public static void main(String[] args) throws IOException {
        if (args.length < 2) {
            System.err.println("Usage: java -jar <jar-name>.jar <base-dir> <map-name>");
            System.exit(1);
        }

        String baseDir = args[0];
        if (baseDir.endsWith("/")) {
            baseDir = baseDir.substring(0, baseDir.length() - 1);
        }

        String mapName = args[1];

        String area = null;
        if (args.length > 2) {
            area = args[2];
        }

        AbstractMap map = MapFactory.createMap(mapName, area);
        map.preProcess();
        readDefs(baseDir, map);
        readAreas(baseDir, map);
        readEntities(baseDir, map);
        map.postProcess();

        String mapJson = map.toJson();
        String mapMetaJson = map.getMeta().toJson();

        Files.write(new File(baseDir + "/maps/" + mapName + ".json").toPath(), mapJson.getBytes());
        Files.write(new File(baseDir + "/maps/" + mapName + ".meta.json").toPath(), mapMetaJson.getBytes());

        System.out.println("Done");
    }

    private static void readEntities(String baseDir, AbstractMap map) {
        // Some entities could not be placed in map until other entities are read. For examples when area filter is
        // set some models can be placed only after player position is defined.
        List<Entity> pendingEntities = new ArrayList<>();

        try (Scanner scanner = new Scanner(new FileInputStream(baseDir + "/maps/" + map.getName() + ".map"))) {
            MutableInt lineNumber = new MutableInt();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();
                lineNumber.increment();

                if (StringUtils.isEmpty(line) || line.startsWith("//"))
                    continue;

                if (line.equals("{")) {
                    int startedAt = lineNumber.intValue();
                    Entity entity = readNextEntity(scanner, lineNumber, map);
                    if (entity == null)
                        System.err.println("Unrecognized entity started at line " + startedAt);
                    else {
                        if (!map.addEntity(entity, false))
                            pendingEntities.add(entity);
                    }
                } else if (line.equals("}"))
                    System.err.println("Unpaired closing curly brace is found at line " + lineNumber);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        for (Entity entity : pendingEntities)
            // It's a last chance for entity to be placed in map
            map.addEntity(entity, true);
    }

    private static Entity readNextEntity(Scanner scanner, MutableInt lineNumber, AbstractMap map) {
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
                    return EntityFactory.createEntity(entityBody.toString(), map);
                openedBraces.pop();
            }

            entityBody.append(line).append("\n");
        }

        System.err.println("Unexpected end of file");
        return null;
    }

    private static void readDefs(String baseDir, AbstractMap map) {
        String[] split = map.getName().split("/");
        File defFile = new File(baseDir + "/def/map_" + split[split.length - 1] + ".def");
        if (defFile.exists())
            try (Scanner scanner = new Scanner(new FileInputStream(defFile))) {
                MutableInt lineNumber = new MutableInt();

                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine().trim();

                    if (StringUtils.isEmpty(line) || line.startsWith("//"))
                        continue;

                    if (line.startsWith("model ")) {
                        int startedAt = lineNumber.intValue();
                        Md5ModelDef modelDef = readNextModelDef(scanner, line, lineNumber, map);
                        if (modelDef == null)
                            System.err.println("Unrecognized model def started at line " + startedAt);
                        else
                            map.getMd5ModelDefs().add(modelDef);
                    }
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
    }

    private static void readAreas(String baseDir, AbstractMap map) {
        try (Scanner scanner = new Scanner(new FileInputStream(baseDir + "/maps/" + map.getName() + ".proc"))) {
            MutableInt lineNumber = new MutableInt();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine().trim();

                if (StringUtils.isEmpty(line) || line.startsWith("//"))
                    continue;

                if (line.startsWith("model {")) {
                    int startedAt = lineNumber.intValue();
                    Area area = readNextArea(scanner, line, lineNumber, map);
                    if (area == null) {
                        System.err.println("Unrecognized area started at line " + startedAt);
                    } else {
                        map.getAreas().add(area);
                    }
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private static Md5ModelDef readNextModelDef(Scanner scanner, String header, MutableInt lineNumber, AbstractMap map) {
        StringBuilder modelDefBody = new StringBuilder(header);
        Deque<Character> openedBraces = new LinkedList<>();
        openedBraces.push('{'); // Brace in header

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (StringUtils.isEmpty(line) || line.startsWith("//"))
                continue;

            if (line.equals("}")) {
                openedBraces.pop();
                if (openedBraces.isEmpty())
                    return map.newMd5ModelDef(modelDefBody.toString());
            } else if (line.contains("{"))
                openedBraces.push('{');

            modelDefBody.append("\n").append(line);
        }

        System.err.println("Unexpected end of file");
        return null;
    }

    private static Area readNextArea(Scanner scanner, String header, MutableInt lineNumber, AbstractMap map) {
        StringBuilder areaBody = new StringBuilder(header);
        Deque<Character> openedBraces = new LinkedList<>();
        openedBraces.push('{'); // Brace in header

        while (scanner.hasNextLine()) {
            String line = scanner.nextLine().trim();

            if (StringUtils.isEmpty(line) || line.startsWith("//"))
                continue;

            if (line.equals("}")) {
                openedBraces.pop();
                if (openedBraces.isEmpty())
                    return map.newArea(areaBody.toString());
            } else if (line.contains("{"))
                openedBraces.push('{');

            areaBody.append("\n").append(line);
        }

        System.err.println("Unexpected end of file");
        return null;
    }
}
