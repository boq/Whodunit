package openmods.whodunit;

import java.util.Map;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin;

public abstract class PluginBase implements IFMLLoadingPlugin {

    @Override
    public String[] getASMTransformerClass() {
        return new String[] { "openmods.whodunit.CallInjectorTransformer" };
    }

    @Override
    public String getModContainerClass() {
        return "openmods.whodunit.Mod";
    }

    @Override
    public String getSetupClass() {
        return "openmods.whodunit.Setup";
    }

    @Override
    public void injectData(Map<String, Object> data) {}

}
