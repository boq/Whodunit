package openmods.whodunit.data;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.BlockingQueue;

import openmods.whodunit.Log;
import openmods.whodunit.data.graph.GraphSerializer;
import openmods.whodunit.data.graph.GraphVisitor;

import com.google.common.collect.*;

public class CallCollector {

    public static final BlockingQueue<CallMarker> CALLS = Queues.newLinkedBlockingDeque();

    private static final Worker WORKER = new Worker();

    public static void registerCall(int location) {
        CALLS.add(new CallMarker(location));
    }

    public static void startWorker() {
        WORKER.setName("Call collector thread");
        WORKER.setDaemon(true);
        WORKER.start();
    }

    public static void resetData() {
        WORKER.resetData();
    }

    public static void visitData(GraphVisitor visitor, Set<Integer> locations) {
        GraphSerializer serializer = new GraphSerializer();

        for (int location : locations)
            WORKER.visitData(location, serializer, visitor);

        visitor.finish();
    }

    private static class Worker extends Thread {

        private final Map<StackTraceElement, CallCounter> callMap = Maps.newHashMap();

        private final Multimap<Integer, StackTraceElement> locationRoots = HashMultimap.create();

        private CallCounter getCounter(StackTraceElement el) {
            CallCounter result = callMap.get(el);

            if (result == null) {
                result = new CallCounter();
                callMap.put(el, result);
            }

            return result;
        }

        public void resetData() {
            callMap.clear();
            locationRoots.clear();
        }

        private synchronized void processCall(CallMarker marker) {
            StackTraceElement[] stacktrace = marker.getStackTrace();

            if (stacktrace.length < 3)
                return;

            StackTraceElement root = stacktrace[2];

            locationRoots.put(marker.location, root);

            CallCounter counter = getCounter(root);

            for (int i = 3; i < stacktrace.length; i++) {
                StackTraceElement el = stacktrace[i];
                counter.addCaller(el);
                counter = getCounter(el);
            }
        }

        public synchronized void visitData(int locationId, GraphSerializer serializer, GraphVisitor visitor) {
            Collection<StackTraceElement> roots = locationRoots.get(locationId);
            serializer.serialize(roots, callMap, visitor);
        }

        @Override
        public void run() {
            CallMarker marker;

            try {
                while ((marker = CALLS.take()) != null) {
                    try {
                        processCall(marker);
                    } catch (Exception e) {
                        Log.warn(e, "Failed to process call");
                    }
                }
            } catch (InterruptedException e) {
                // NO-OP, finish
            }
        }
    }
}
