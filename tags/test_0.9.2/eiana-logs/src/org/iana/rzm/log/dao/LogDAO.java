package org.iana.rzm.log.dao;

import org.iana.rzm.log.LogEntry;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface LogDAO {

    public void create(LogEntry log);
    
}
