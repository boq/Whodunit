package openmods.whodunit.data.graph;

public interface GraphVisitor {
    public void visitVertex(int index, StackTraceElement value);

    public void visitEdge(int from, int to, int weight);

    public void finish();
}
