package org.iana.rzm.log;

import org.iana.objectdiff.Change;
import org.iana.rzm.common.TrackedObject;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

/**
 * A single log entry.
 *
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class LogEntry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;

    private String userName;
    private String sessionId;
    private Timestamp timestamp;

    private String action;

    private Long objectId;
    private String objectClassName;

    @OneToMany(cascade = CascadeType.ALL, targetEntity = Change.class)
    private List<Change> differences;

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

    public LogEntry(String userName, String sessionId, Timestamp timestamp, String action, TrackedObject object, List<Change> difference) {
        this.userName = userName;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.action = action;
        if (object != null) {
            this.objectId = object.getObjId();
            this.objectClassName = object.getClass().getName();
        }
        this.differences = difference;
    }

    public LogEntry(String userName, String sessionId, Timestamp timestamp, String action, long objectId, String objectClassName, List<Change> difference) {
        this.userName = userName;
        this.sessionId = sessionId;
        this.timestamp = timestamp;
        this.action = action;
        this.objectId = objectId;
        this.objectClassName = objectClassName;
        this.differences = difference;
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

    public List<Change> getDifferences() {
        return differences;
    }
}
