package org.iana.config;

import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface ConfigDAO {

    AbstractParameter getParameter(String owner, String name);

    void addParameter(AbstractParameter parameter);

    void removeParameter(AbstractParameter parameter);

    void removeParameter(String owner, String name);

    Set<String> getParameterNames(String owner);

    Set<String> getSubConfigNames(String owner);
}
