package openmods.whodunit.injection;

import java.util.Collection;

import openmods.whodunit.Log;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import com.google.common.collect.ImmutableList;

public class ClassInjector extends ClassVisitor {

    private final Collection<MethodMatcher> matchers;

    private final String rawClsName;

    public ClassInjector(ClassVisitor cv, String rawClsName, Collection<MethodMatcher> matchers) {
        super(Opcodes.ASM4, cv);
        this.matchers = ImmutableList.copyOf(matchers);
        this.rawClsName = rawClsName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
        for (MethodMatcher matcher : matchers)
            if (matcher.match(name, desc)) {
                Log.info("Injecting logger for location %d in method %s.%s(%s)", matcher.location, rawClsName, name, desc);
                return new MethodInjector(parent, matcher.location);
            }

        return parent;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();

        for (MethodMatcher matcher : matchers)
            if (!matcher.hasMatched())
                Log.warn("Matcher %s has not matched, not capturing part of calls", matcher);
    }

}
