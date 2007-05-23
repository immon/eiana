package pl.nask.logs.dao;

import pl.nask.logs.LogEntry;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface LogDAO {

    public void create(LogEntry log);
    
}
