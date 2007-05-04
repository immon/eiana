package org.iana.rzm.log;

import org.iana.objectdiff.Change;
import org.iana.rzm.user.RZMUser;

import java.sql.Timestamp;

/**
 * A single log entry.
 *
 * @author Patrycja Wegrzynowicz
 */
public class LogEntry {

    private RZMUser user;

    private Timestamp timestamp;

    private String action;

    private Change difference;

    private LogEntry() {
    }

    public LogEntry(RZMUser user) {
        this.user = user;
        this.timestamp = new Timestamp(System.currentTimeMillis());
    }
}
