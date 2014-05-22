package openmods.whodunit.injection;

import openmods.whodunit.utils.VisitorHelper;
import cpw.mods.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

public class MethodMatcher {
    private final String clsName;
    private final String description;
    private final String srgName;
    private final String mcpName;

    public final int location;

    private boolean hasMatched;

    public MethodMatcher(String clsName, String description, String mcpName, String srgName, int location) {
        this.clsName = clsName;
        this.description = description;
        this.srgName = srgName;
        this.mcpName = mcpName;
        this.location = location;
    }

    private boolean tryMatch(String methodName, String methodDesc) {
        if (!methodDesc.equals(description))
            return false;
        if (methodName.equals(mcpName))
            return true;
        if (!VisitorHelper.useSrgNames())
            return false;
        String mapped = FMLDeobfuscatingRemapper.INSTANCE.mapMethodName(clsName, methodName, methodDesc);
        return mapped.equals(srgName);
    }

    public boolean match(String methodName, String methodDesc) {
        if (tryMatch(methodName, methodDesc)) {
            hasMatched = true;
            return true;
        }

        return false;
    }

    @Override
    public String toString() {
        return String.format("%s.[%s;%s] %s", clsName, mcpName, srgName, description);
    }

    public boolean hasMatched() {
        return hasMatched;
    }
}
