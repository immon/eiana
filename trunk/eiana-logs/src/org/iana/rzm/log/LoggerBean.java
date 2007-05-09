package org.iana.rzm.log;

import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.log.dao.LogDAO;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.Change;
import org.iana.objectdiff.ChangeDetector;

import java.sql.Timestamp;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Patrycja Wegrzynowicz
 */
public class LoggerBean implements Logger {

    private DiffConfiguration diffConfiguration;
    private LogDAO dao;

    public LoggerBean(DiffConfiguration diffConfiguration, LogDAO dao) {
        CheckTool.checkNull(diffConfiguration, "diff config");
        CheckTool.checkNull(dao, "log dao");
        this.diffConfiguration = diffConfiguration;
        this.dao = dao;
    }

    public void addLog(String userName, String sessionId, String action, TrackedObject object, Object[] parameters) {
        CheckTool.checkNull(object, "logged object");
        Timestamp now = now();
        setTimestamps(object, now, userName);
        dao.create(new LogEntry(userName, sessionId, now, action, object));
    }

    public void addLog(String userName, String sessionID, String action, TrackedObject object) {
        CheckTool.checkNull(object, "logged object");
        Timestamp now = now();
        setTimestamps(object, now, userName);
        dao.create(new LogEntry(userName, sessionID, now, action, object));
    }

    public void addLog(String userName, String sessionID, String action, TrackedObject object, TrackedObject oldObject) {
        Timestamp now = now();
        if (object != null) setTimestamps(object, now, userName);
        Change change = ChangeDetector.diff(oldObject, object, diffConfiguration);
        List<Change> diff = new ArrayList<Change>();
        diff.add(change);
        dao.create(new LogEntry(userName, sessionID, now, action, object, diff));
    }

    private void setTimestamps(TrackedObject object, Timestamp timestamp, String userName) {
        if (object.getCreated() == null) {
            object.setCreated(timestamp);
            object.setCreatedBy(userName);
        }
        else {
            object.setModified(timestamp);
            object.setModifiedBy(userName);
        }
    }

    private Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }
}
