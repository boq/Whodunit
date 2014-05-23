package openmods.whodunit.data.graph;

import java.io.File;

public class GraphvizVisitor extends PrintWriterVisitor {

    private static final String PREAMBLE =
            "digraph G {\n" +
                    "graph [\n" +
                    "rankdir = BT" +
                    "];\n" +
                    "node [\n" +
                    "shape = box\n" +
                    "];" +
                    "\n\n";

    public GraphvizVisitor(File outputFile) {
        super(outputFile);
        output.print(PREAMBLE);
    }

    @Override
    public void visitVertex(int index, StackTraceElement value, boolean isRoot) {
        String format = isRoot
                ? "node_%d [label = \"%s\", color = \"#00AAAA\", fillcolor = \"#99CCCC\", style = filled];\n"
                : "node_%d [label = \"%s\"];\n";
        output.format(format, index, value.toString());
    }

    @Override
    public void visitEdge(int from, int to, int weight) {
        String format;
        if (weight > 500) {
            format = "node_%s -> node_%d [label = \"%d\", color = \"#ff0000\", penwidth = 2];\n";
        } else if (weight > 50) {
            format = "node_%s -> node_%d [label = \"%d\", color = \"#ff8c00\", penwidth = 1.5];\n";
        } else if (weight > 5) {
            format = "node_%s -> node_%d [label = \"%d\", color = \"#ffd700\"];\n";
        } else {
            format = "node_%s -> node_%d [label = \"%d\"];\n";
        }
        output.format(format, to, from, weight);
    }

    @Override
    public void finish() {
        output.println("}");
        super.finish();
    }

}
