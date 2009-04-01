package org.iana.rzm.facade.admin.config.binded;

import org.apache.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public abstract class BindedParameter {

    private Logger logger = Logger.getLogger(BindedParameter.class);

    Map<String, String> values = new HashMap<String, String>();

    protected BindedParameter() {
    }

    protected BindedParameter(Map<String, String> values) {
        this.values = values;
    }

    protected String getValue(String name) {
        return values.get(name);
    }

    protected void setValue(String name, Object value) {
        if (values.containsKey(name))
                    logger.debug("Value: " + values.get(name) + " for parameter: " + name + " replaced by: " + value);
        
        if (value == null) {
            values.put(name, null);
        } else {
            values.put(name, value.toString());
        }
    }

    protected Integer getInetegerValue(String name) {
        String value = getValue(name);

        return (value == null)? null : Integer.parseInt(value);
    }

    protected Boolean getBooleanValue(String name) {
        String value = getValue(name);

        return (value == null)? null : Boolean.parseBoolean(value);
    }

    public Map<String, String> getValuesMap() {
        return values;
    }

}
