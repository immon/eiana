package org.iana.criteria;

import java.util.List;
import java.util.Set;

/**
 * The base interface of config hierarchy that provides a set of methods
 * to fetch String values of parameters stored in a config.
 *
 * Each fetch method must return null to indicate that the value does not exist in a config.
 *
 * @author Patrycja Wegrzynowicz
 */
public interface Config {

    String getParameter(String name);

    List<String> getParameterList(String name);

    Set<String> getParameterSet(String name);

    Config getConfig(String name);

    Set<String> getParameterNames();

    Boolean getBooleanParameter(String name);

    Integer getIntegerParameter(String name);

    Long getLongParameter(String name);

    List<Boolean> getBooleanParameterList(String name);

    List<Integer> getIntegerParameterList(String name);

    List<Long> getLongParameterList(String name);

    Set<Boolean> getBooleanParameterSet(String name);

    Set<Integer> getIntegerParameterSet(String name);
    
    Set<Long> getLongParameterSet(String name);

}
