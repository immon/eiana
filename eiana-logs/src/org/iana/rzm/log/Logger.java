package org.iana.rzm.log;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Logger {

    void addLog(Log entry) throws LogException;
}
