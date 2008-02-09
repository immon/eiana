package org.iana.config;

import org.iana.config.impl.ConfigException;

import java.util.List;
import java.util.Set;

/**
 * The base interface of config hierarchy that provides a set of methods
 * to fetch String values of parameters stored in a config.
 * <p/>
 * Each fetch method must return null to indicate that the value does not exist in a config.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
 */
public interface Config {

    final static String DEFAULT_OWNER = "DefaultConfig";

    String getParameter(String name) throws ConfigException;

    String getParameter(String name, String defaultValue) throws ConfigException;

    List<String> getParameterList(String name) throws ConfigException;

    List<String> getParameterList(String name, List<String> defaultValue) throws ConfigException;

    Set<String> getParameterSet(String name) throws ConfigException;

    Set<String> getParameterSet(String name, Set<String> defaultValue) throws ConfigException;

    Config getSubConfig(String name) throws ConfigException;

    Set<String> getSubConfigNames() throws ConfigException;

    Set<String> getParameterNames() throws ConfigException;

    Boolean getBooleanParameter(String name) throws ConfigException;

    Boolean getBooleanParameter(String name, Boolean defaultValue) throws ConfigException;

    Integer getIntegerParameter(String name) throws ConfigException;

    Integer getIntegerParameter(String name, Integer defaultValue) throws ConfigException;

    Long getLongParameter(String name) throws ConfigException;

    Long getLongParameter(String name, Long defaultValue) throws ConfigException;

    List<Boolean> getBooleanParameterList(String name) throws ConfigException;

    List<Boolean> getBooleanParameterList(String name, List<Boolean> defaultValue) throws ConfigException;

    List<Integer> getIntegerParameterList(String name) throws ConfigException;

    List<Integer> getIntegerParameterList(String name, List<Integer> defaultValue) throws ConfigException;

    List<Long> getLongParameterList(String name) throws ConfigException;

    List<Long> getLongParameterList(String name, List<Long> defaultValue) throws ConfigException;

    Set<Boolean> getBooleanParameterSet(String name) throws ConfigException;

    Set<Boolean> getBooleanParameterSet(String name, Set<Boolean> defaultValue) throws ConfigException;

    Set<Integer> getIntegerParameterSet(String name) throws ConfigException;

    Set<Integer> getIntegerParameterSet(String name, Set<Integer> defaultValue) throws ConfigException;

    Set<Long> getLongParameterSet(String name) throws ConfigException;

    Set<Long> getLongParameterSet(String name, Set<Long> defaultValue) throws ConfigException;

}
