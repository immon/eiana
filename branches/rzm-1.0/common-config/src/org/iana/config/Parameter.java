package org.iana.config;

import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public interface Parameter {

    public static final long DAY = 24 * 60 * 60 * 1000;

    public String getName();

    public String getParameter();

    public List<String> getParameterList();

    public Set<String> getParameterSet();
}
