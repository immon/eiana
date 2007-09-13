package org.iana.config;

import java.util.List;
import java.util.ArrayList;
import java.util.Set;

/**
 * The config composed of a list of other configs. The configs are searched
 * in a list order, the first value found is returned.  
 *
 * @author Patrycja Wegrzynowicz
 */
public class CompositeConfig extends AbstractConfig implements Config {

    private List<Config> configs = new ArrayList<Config>();

    public CompositeConfig(List<Config> configs) {

    }
    
    public void addConfig(Config config) {

    }

    public String getParameter(String name) {
        return null;
    }

    public List<String> getParameterList(String name) {
        return null;
    }

    public Set<String> getParameterSet(String name) {
        return null;
    }

    public Config getConfig(String name) {
        return null;
    }

    public Set<String> getParameterNames() {
        return null;
    }
}
