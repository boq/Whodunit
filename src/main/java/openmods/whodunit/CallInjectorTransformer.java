package openmods.whodunit;

import java.util.Collection;

import net.minecraft.launchwrapper.IClassTransformer;
import openmods.whodunit.injection.ClassInjector;
import openmods.whodunit.injection.MatcherMap;
import openmods.whodunit.injection.MethodMatcher;
import openmods.whodunit.utils.VisitorHelper;
import openmods.whodunit.utils.VisitorHelper.TransformProvider;

import org.objectweb.asm.ClassVisitor;

public class CallInjectorTransformer implements IClassTransformer {

    private static MatcherMap matcherMap;

    public static void injectMatchers(MatcherMap matchers) {
        matcherMap = matchers;
    }

    @Override
    public byte[] transform(final String rawName, String transformedName, byte[] bytes) {
        if (bytes == null || matcherMap == null)
            return bytes;

        final Collection<MethodMatcher> matchers = matcherMap.getMatchers(transformedName);

        if (!matchers.isEmpty()) {
            return VisitorHelper.apply(bytes, 0, new TransformProvider() {
                @Override
                public ClassVisitor createVisitor(ClassVisitor cv) {
                    return new ClassInjector(cv, rawName, matchers);
                }
            });
        }

        return bytes;
    }

}
