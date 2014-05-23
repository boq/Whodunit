package openmods.whodunit.data.graph;

import java.io.File;

import com.google.gson.*;

public class JsonVisitor extends PrintWriterVisitor {

    private final JsonObject vertices = new JsonObject();

    private final JsonArray edges = new JsonArray();

    private final Gson gson;

    public JsonVisitor(File outputFile, boolean prettyPrint) {
        super(outputFile);

        GsonBuilder builder = new GsonBuilder();
        if (prettyPrint)
            builder.setPrettyPrinting();
        gson = builder.create();
    }

    @Override
    public void visitVertex(int index, StackTraceElement value, boolean isRoot) {
        JsonObject vertex = gson.toJsonTree(value).getAsJsonObject();
        if (isRoot)
            vertex.addProperty("root", true);
        vertices.add(Integer.toString(index), vertex);
    }

    @Override
    public void visitEdge(int from, int to, int weight) {
        JsonObject edge = new JsonObject();
        edge.addProperty("from", from);
        edge.addProperty("to", to);
        edge.addProperty("weight", weight);
        edges.add(edge);
    }

    @Override
    public void finish() {
        JsonObject doc = new JsonObject();
        doc.add("vertices", vertices);
        doc.add("edges", edges);
        gson.toJson(doc, output);
        super.finish();
    }
}
