package org.iana.config;

import org.iana.config.impl.ConfigException;

import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public interface ParameterManager {

    public String getParameter(String owner, String name) throws ConfigException;

    public List<String> getParameterList(String owner, String name) throws ConfigException;

    public Set<String> getParameterSet(String owner, String name) throws ConfigException;

    public Set<String> getParameterNames(String owner, String name) throws ConfigException;

    public Set<String> getSubConfigNames(String owner, String name) throws ConfigException;

}
