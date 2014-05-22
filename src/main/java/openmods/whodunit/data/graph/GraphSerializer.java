package openmods.whodunit.data.graph;

import java.util.*;

import openmods.whodunit.data.CallCounter;

import com.google.common.collect.*;

public class GraphSerializer {

    private int counter;

    private final Map<StackTraceElement, Integer> vertices = Maps.newHashMap();

    private final Set<StackTraceElement> alreadyVisited = Sets.newHashSet();

    private int getIdForVertex(StackTraceElement el) {
        Integer result = vertices.get(el);

        if (result == null) {
            result = counter++;
            vertices.put(el, result);
        }

        return result;
    }

    public void serialize(Collection<StackTraceElement> roots, Map<StackTraceElement, CallCounter> data, GraphVisitor visitor) {
        Queue<StackTraceElement> todo = Lists.newLinkedList(roots);

        StackTraceElement callee;
        while ((callee = todo.poll()) != null) {
            if (alreadyVisited.contains(callee))
                continue;
            alreadyVisited.add(callee);
            final CallCounter counter = data.get(callee);
            final int calleeId = getIdForVertex(callee);

            visitor.visitVertex(calleeId, callee);

            for (Multiset.Entry<StackTraceElement> callerCount : counter.callers.entrySet()) {
                final StackTraceElement caller = callerCount.getElement();
                todo.add(caller);
                int callerId = getIdForVertex(caller);
                visitor.visitEdge(calleeId, callerId, callerCount.getCount());
            }
        }
    }
}
