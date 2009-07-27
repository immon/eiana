package org.iana.config;

import org.iana.config.impl.ConfigException;

import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public interface ConfigDAO {

    Parameter getParameter(String owner, String name) throws ConfigException;

    List<Parameter> getParameters() throws ConfigException;
    
    void addParameter(Parameter parameter) throws ConfigException;

    void removeParameter(Parameter parameter) throws ConfigException;

    void removeParameter(String owner, String name) throws ConfigException;

    void updateParameter(Parameter parameter) throws ConfigException;

    Set<String> getParameterNames(String owner, String name) throws ConfigException;

    Set<String> getSubConfigNames(String owner, String name) throws ConfigException;
}
