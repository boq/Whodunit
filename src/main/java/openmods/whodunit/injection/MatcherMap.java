package openmods.whodunit.injection;

import java.util.Collection;

import openmods.whodunit.config.MethodDescriptor;
import openmods.whodunit.data.LocationManager;
import openmods.whodunit.utils.VisitorHelper;

import org.objectweb.asm.Type;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;

import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class MatcherMap {

    private final LocationManager locationManager;

    public MatcherMap(LocationManager locationManager) {
        this.locationManager = locationManager;
    }

    private final Multimap<String, MethodMatcher> matchers = HashMultimap.create();

    public String getClassName(String name) {
        name = name.replace('.', '/');
        return VisitorHelper.useSrgNames() ? FMLDeobfuscatingRemapper.INSTANCE.unmap(name) : name;
    }

    public void markMethodForInjection(String deobfCls, String description, String mcpName, String srgName, int location) {
        String obfCls = getClassName(deobfCls);
        matchers.put(deobfCls, new MethodMatcher(obfCls, description, mcpName, srgName, location));
    }

    public void createFromDescription(String name, MethodDescriptor descriptor) {
        Preconditions.checkNotNull(descriptor.cls, "Class not set for method %s", name);
        Preconditions.checkNotNull(descriptor.returnType, "Return type not set for method %s", name);
        Preconditions.checkNotNull(descriptor.mcpName, "MCP name not set for method %s", name);
        Preconditions.checkNotNull(descriptor.srgName, "SRG name not set for method %s", name);
        Preconditions.checkNotNull(descriptor.argTypes, "Arguments not set for method %s", name);

        int location = locationManager.getOrCreateIdForLocation(name);

        final String[] argNames = descriptor.argTypes;

        Type[] argumentTypes = new Type[argNames.length];

        for (int i = 0; i < argNames.length; i++) {
            final String typeDescriptor = argNames[i];
            argumentTypes[i] = convertToType(typeDescriptor);
        }

        Type returnType = convertToType(descriptor.returnType);

        Type desc = Type.getMethodType(returnType, argumentTypes);

        markMethodForInjection(descriptor.cls, desc.getDescriptor(), descriptor.mcpName, descriptor.srgName, location);
    }

    protected Type convertToType(final String typeDescriptor) {
        Type tmp = Type.getType(typeDescriptor);
        if (tmp.getSort() == Type.OBJECT) {
            String originalCls = tmp.getInternalName();
            String actualName = getClassName(originalCls);
            tmp = Type.getObjectType(actualName);
        }
        return tmp;
    }

    public Collection<MethodMatcher> getMatchers(String className) {
        return matchers.get(className);
    }

}
