package org.iana.rzm.facade.admin.config.impl;

import org.iana.config.impl.ConfigException;

import java.util.Map;

/**
 * @author Patrycja Wegrzynowicz
*/
public interface ConfigParameterRetriever {

    public Map<String, String> getParameter(String name) throws ConfigException;

}
