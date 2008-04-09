package org.iana.rzm.startup.jbpm;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface JbpmCommand {
    public long execute(Object arg) throws Exception;
}
