package org.iana.rzm.log;

import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.log.dao.LogDAO;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.objectdiff.Change;
import org.iana.objectdiff.ChangeDetector;

import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 */
public class LoggerBean implements Logger {

    DiffConfiguration diffConfiguration;
    LogDAO dao;

    public void addLog(String userName, String sessionId, String action, TrackedObject object) {
        CheckTool.checkNull(object, "logged object");
        Timestamp now = now();
        object.setCreated(now);
        object.setCreatedBy(userName);
        dao.create(new LogEntry(userName, sessionId, now, action, object));
    }

    public void addLog(String userName, String sessionId, String action, TrackedObject src, TrackedObject dst) {
        CheckTool.checkNull(src, "source logged object");
        CheckTool.checkNull(src, "destination logged object");
        Timestamp now = now();
        dst.setModified(now);
        dst.setModifiedBy(userName);
        Change change = ChangeDetector.diff(src, dst, diffConfiguration);
        dao.create(new LogEntry(userName, sessionId, now, action, dst, change));
    }

    private Timestamp now() {
        return new Timestamp(System.currentTimeMillis());
    }
}
