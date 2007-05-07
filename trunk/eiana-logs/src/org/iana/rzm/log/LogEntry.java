package org.iana.rzm.log;

import org.iana.objectdiff.Change;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.common.TrackedObject;

import javax.persistence.Entity;
import java.sql.Timestamp;

/**
 * A single log entry.
 *
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class LogEntry {

    private String userName;
    private String sessionId;
    private Timestamp timestamp;

    private String action;

    private long objectId;
    private String objectClassName;

    private Change difference;

    private LogEntry() {
    }

    public LogEntry(String userName, String sessionId, Timestamp timestamp, String action, TrackedObject object) {
        this.userName = userName;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.action = action;
        this.objectId = object.getObjId();
        this.objectClassName = object.getClass().getName();
    }

    public LogEntry(String userName, String sessionId, Timestamp timestamp, String action, TrackedObject object, Change difference) {
        this.userName = userName;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.action = action;
        this.objectId = object.getObjId();
        this.objectClassName = object.getClass().getName();
        this.difference = difference;
    }

    public LogEntry(String userName, String sessionId, Timestamp timestamp, String action, long objectId, String objectClassName, Change difference) {
        this.userName = userName;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.action = action;
        this.objectId = objectId;
        this.objectClassName = objectClassName;
        this.difference = difference;
    }       

    public String getUserName() {
        return userName;
    }

    public String getSessionId() {
        return sessionId;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public String getAction() {
        return action;
    }

    public long getObjectId() {
        return objectId;
    }

    public String getObjectClassName() {
        return objectClassName;
    }

    public Change getDifference() {
        return difference;
    }
}
