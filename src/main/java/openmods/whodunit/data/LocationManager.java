package openmods.whodunit.data;

import java.util.Collection;
import java.util.Map;

import com.google.common.collect.Maps;

public class LocationManager {
    private int idCounter = 0;
    private final Map<String, Integer> locations = Maps.newHashMap();

    public synchronized int getOrCreateIdForLocation(String location) {
        Integer id = locations.get(location);

        if (id == null) {
            id = idCounter++;
            locations.put(location, id);
        }

        return id;
    }

    public synchronized Integer getIdForLocation(String location) {
        return locations.get(location);
    }

    public Collection<String> listLocationNames() {
        return locations.keySet();
    }
}
