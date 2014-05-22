package openmods.whodunit.config;

import java.io.*;
import java.util.Map;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.gson.Gson;

public class Config {
    public Map<String, MethodDescriptor> methods = Maps.newHashMap();

    public static Config readConfig(File configFile) throws IOException {
        Gson gson = new Gson();
        InputStream input = new FileInputStream(configFile);
        try {
            Reader reader = new InputStreamReader(input, Charsets.UTF_8);
            return gson.fromJson(reader, Config.class);
        } finally {
            input.close();
        }
    }

}
