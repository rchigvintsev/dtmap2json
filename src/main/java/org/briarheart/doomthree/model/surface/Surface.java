package org.briarheart.doomthree.model.surface;

import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.mutable.MutableInt;
import org.briarheart.doomthree.model.surface.material.Materials;
import org.briarheart.doomthree.model.surface.physics.CollisionModel;
import org.briarheart.doomthree.util.BoundingBox;
import org.briarheart.doomthree.util.Vector2;
import org.briarheart.doomthree.util.Vector3;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.lang.Double.parseDouble;
import static java.lang.Integer.parseInt;

/**
 * @author Roman Chigvintsev
 */
public class Surface {
    private static final Pattern HEADER_PATTERN = Pattern
            .compile("/\\* (surface \\d+) \\*/ \\{ \"([\\w/_]+)\" /\\* numVerts = \\*/ (\\d+)");

    private String name;
    private String materialName;
    private int numberOfVertices;
    private Vertex[] vertices;
    private Face[] faces;
    private CollisionModel collisionModel;
    private BoundingBox boundingBox = new BoundingBox();

    public Surface(String surfaceBody) {
        parse(surfaceBody);
    }

    public String getName() {
        return name;
    }

    public String getMaterialName() {
        return materialName;
    }

    public Vertex[] getVertices() {
        return vertices;
    }

    public Face[] getFaces() {
        return faces;
    }

    public BoundingBox getBoundingBox() {
        return boundingBox;
    }

    @Override
    public String toString() {
        return toJson();
    }

    public String toJson() {
        StringBuilder json = new StringBuilder("{\"name\":\"").append(name).append("\",");
        json.append("\"geometry\":{\"vertices\":[");
        for (int i = 0; i < vertices.length; i++) {
            Vertex vertex = vertices[i];
            if (i > 0)
                json.append(",");
            json.append(vertex);
        }
        json.append("],\"faces\":[");
        for (int i = 0; i < faces.length; i++) {
            Face face = faces[i];
            if (i > 0)
                json.append(",");
            json.append(face);
        }
        json.append("],\"uvs\":[");
        for (int i = 0; i < faces.length; i++) {
            Face face = faces[i];
            if (i > 0)
                json.append(",");
            json.append("[").append(vertices[face.a].uv).append(",");
            json.append(vertices[face.b].uv).append(",");
            json.append(vertices[face.c].uv).append("]");
        }
        json.append("]},\"material\":\"").append(materialName).append("\"");
        if (collisionModel != null)
            json.append(",\"cm\":").append(collisionModel.toJson());
        return json.append("}").toString();
    }

    protected void parse(String surfaceBody) {
        Validate.notEmpty(surfaceBody, "Surface body cannot be empty");

        Matcher matcher = HEADER_PATTERN.matcher(surfaceBody);
        if (!matcher.find()) {
            System.err.println("Surface body does not contain surface name and number of vertices");
            return;
        }

        name = matcher.group(1);
        materialName = matcher.group(2);
        numberOfVertices = parseInt(matcher.group(3));
        MutableInt i = new MutableInt();
        parseVertices(surfaceBody, i);
        parseFaces(surfaceBody, i);
        if (!Materials.isDecal(materialName))
            collisionModel = new CollisionModel(this);
    }

    protected void parseVertices(String surfaceBody, MutableInt i) {
        vertices = new Vertex[numberOfVertices];
        int vertexIndex = 0;
        boolean inVertex = false;
        StringBuilder vertexString = new StringBuilder();
        while (i.intValue() < surfaceBody.length() && vertexIndex < numberOfVertices) {
            char ch = surfaceBody.charAt(i.intValue());
            if (ch == '(') {
                vertexString = new StringBuilder();
                inVertex = true;
            } else if (ch == ')') {
                if (!inVertex)
                    System.err.println("Unpaired closing parentheses found in surface \"" + name + "\"");
                else {
                    inVertex = false;
                    if (vertexString.length() == 0)
                        System.err.println("Empty vertex definition found in surface \"" + name + "\"");
                    else {
                        String[] vertexComponents = vertexString.toString().trim().split(" ");
                        Vector3 position = new Vector3(parseDouble(vertexComponents[1]),
                                parseDouble(vertexComponents[2]), parseDouble(vertexComponents[0]));
                        Vector2 uv = new Vector2(parseDouble(vertexComponents[3]), parseDouble(vertexComponents[4]));
                        Vector3 normal = new Vector3(parseDouble(vertexComponents[6]), parseDouble(vertexComponents[7]),
                                parseDouble(vertexComponents[5]));
                        vertices[vertexIndex++] = new Vertex(position, uv, normal);
                        boundingBox.checkBoundaries(position);
                    }
                }
            } else
                vertexString.append(ch);
            i.increment();
        }
    }

    protected void parseFaces(String surfaceBody, MutableInt i) {
        if (i.intValue() >= surfaceBody.length()) {
            System.err.println("Surface \"" + name + "\" does not contain information about faces");
            return;
        }

        List<Face> faces = new ArrayList<>();
        String[] split = surfaceBody.substring(i.intValue()).trim().split("[ \n}]");
        int[] faceArray = null;
        for (int j = 0; j < split.length; j++) {
            if (j % 3 == 0) {
                if (faceArray != null)
                    faces.add(new Face(faces.size() + 1, faceArray, vertices[faceArray[0]].normal));
                faceArray = new int[3];
            }
            assert faceArray != null;
            faceArray[j % 3] = parseInt(split[j]);
        }
        if (faceArray != null)
            faces.add(new Face(faces.size() + 1, faceArray, vertices[faceArray[0]].normal));
        this.faces = faces.toArray(new Face[faces.size()]);
    }
}
