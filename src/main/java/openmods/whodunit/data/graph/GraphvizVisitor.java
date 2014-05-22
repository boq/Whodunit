package openmods.whodunit.data.graph;

import java.io.File;

public class GraphvizVisitor extends PrintWriterVisitor {

    private static final String PREAMBLE =
            "digraph G {\n" +
                    "graph [\n" +
                    "rankdir = \"BT\"" +
                    "];\n" +
                    "node [\n" +
                    "shape = \"rectangle\"\n" +
                    "];" +
                    "\n\n";

    public GraphvizVisitor(File outputFile) {
        super(outputFile);
        output.print(PREAMBLE);
    }

    @Override
    public void visitVertex(int index, StackTraceElement value) {
        output.format("node_%d [label = \"%s\"];\n", index, value.toString());
    }

    @Override
    public void visitEdge(int from, int to, int weight) {
        output.format("node_%s -> node_%d [label = \"%d\"];\n", to, from, weight);
    }

    @Override
    public void finish() {
        output.println("}");
        super.finish();
    }

}
