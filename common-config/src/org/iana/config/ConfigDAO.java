package org.iana.config;

import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public interface ConfigDAO {

    Parameter getParameter(String owner, String name);

    void addParameter(Parameter parameter);

    void removeParameter(Parameter parameter);

    void removeParameter(String owner, String name);

    Set<String> getParameterNames(String owner, String name);

    Set<String> getSubConfigNames(String owner, String name);
}
