package openmods.whodunit;

import cpw.mods.fml.relauncher.IFMLLoadingPlugin.Name;
import cpw.mods.fml.relauncher.IFMLLoadingPlugin.TransformerExclusions;

@Name("Whodunit")
@TransformerExclusions("openmods.whodunit")
public class Plugin extends PluginBase {

    @Override
    public String getAccessTransformerClass() {
        return null;
    }

}
