package org.iana.config;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface ConfigDAO {

    Parameter getParameter(String owner, String name);

    void addParameter(Parameter parameter);

    void removeParameter(Parameter parameter); 

    void removeParameter(String owner, String name);
}
