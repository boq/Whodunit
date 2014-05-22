package openmods.whodunit;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Map;

import openmods.whodunit.config.Config;
import openmods.whodunit.config.MethodDescriptor;
import openmods.whodunit.data.CallCollector;
import openmods.whodunit.data.LocationManager;
import openmods.whodunit.injection.MatcherMap;
import cpw.mods.fml.relauncher.IFMLCallHook;

public class Setup implements IFMLCallHook {

    public static File mcLocation;

    public final static LocationManager locations = new LocationManager();

    @Override
    public void injectData(Map<String, Object> data) {
        mcLocation = (File)data.get("mcLocation");
    }

    @Override
    public Void call() throws Exception {
        File configDir = getConfigDir();
        File configFile = new File(configDir, "whodunit.json");

        MatcherMap matchers = new MatcherMap(locations);
        CallInjectorTransformer.injectMatchers(matchers);
        try {
            Config config = Config.readConfig(configFile);

            for (Map.Entry<String, MethodDescriptor> method : config.methods.entrySet())
                matchers.createFromDescription(method.getKey(), method.getValue());

            CallCollector.startWorker();
        } catch (FileNotFoundException e) {
            Log.warn("Config file %s does not exist, Whodunit will be disabled", configFile.getAbsolutePath());
        } catch (Exception e) {
            Log.warn(e, "Failed to read config %s, Whodunit will be disabled", configFile.getAbsolutePath());
        }

        return null;
    }

    private static File getDir(String name) {
        File dir = new File(mcLocation, name);
        dir.mkdir();
        return dir;
    }

    public static File getDumpDir() {
        return getDir("dumps");
    }

    public static File getConfigDir() {
        return getDir("config");
    }

}
