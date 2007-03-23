package org.iana.rzm.trans.change;

import org.iana.rzm.common.validators.CheckTool;

import java.util.*;
import java.sql.Timestamp;
import java.net.URL;

/**
 * @author Patrycja Wegrzynowicz
 */
public class DiffConfiguration {

    private Set<Class> simpleClasses = new HashSet<Class>();
    private Map<Class, ObjectConfiguration> objectConfigs = new HashMap<Class, ObjectConfiguration>();

    public DiffConfiguration() {
        this(initSimpleClasses(), new HashMap<Class, ObjectConfiguration>());
     }

    public DiffConfiguration(Set<Class> simpleClasses, Map<Class, ObjectConfiguration> objectFields) {
        setSimpleClasses(simpleClasses);
        setObjectConfigurations(objectFields);
    }

    public void setSimpleClasses(Set<Class> simpleClasses) {
        CheckTool.checkNull(simpleClasses, "simple classes");
        this.simpleClasses.clear();
        this.simpleClasses.addAll(simpleClasses);
    }

    public void setObjectConfigurations(Map<Class, ObjectConfiguration> configs) {
        CheckTool.checkNull(configs, "object configs map");
        this.objectConfigs.clear();
        this.objectConfigs.putAll(configs);
    }

    public void addObjectConfiguration(Class clazz, ObjectConfiguration config) {
        CheckTool.checkNull(clazz, "object class");
        CheckTool.checkNull(config, "object config");
        this.objectConfigs.put(clazz, config);
    }

    public boolean isSimple(Class clazz) {
        return simpleClasses.contains(clazz);
    }

    public boolean isObject(Class clazz) {
        return objectConfigs.containsKey(clazz);
    }

    public ObjectConfiguration getObjectConfig(Class clazz) {
        return objectConfigs.get(clazz);
    }

    static Set<Class> initSimpleClasses() {
        Set<Class> simpleClasses = new HashSet<Class>();
        simpleClasses.add(String.class);
        simpleClasses.add(Boolean.class);
        simpleClasses.add(Date.class);
        simpleClasses.add(Timestamp.class);
        simpleClasses.add(URL.class);
        return simpleClasses;
    }
}
