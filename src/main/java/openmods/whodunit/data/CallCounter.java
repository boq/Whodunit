package openmods.whodunit.data;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;

public class CallCounter {

    public Multiset<StackTraceElement> callers = HashMultiset.create();

    public void addCaller(StackTraceElement caller) {
        callers.add(caller);
    }

}
