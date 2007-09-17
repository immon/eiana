package org.iana.config;

import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public interface Parameter {

    public String getName();

    public String getParameter();

    public List<String> getParameterList();

    public Set<String> getParameterSet();
}
