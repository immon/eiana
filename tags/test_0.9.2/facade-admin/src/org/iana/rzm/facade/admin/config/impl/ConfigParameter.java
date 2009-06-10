package org.iana.rzm.facade.admin.config.impl;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ConfigParameter {

    private String name;

    private String value;

    public ConfigParameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
