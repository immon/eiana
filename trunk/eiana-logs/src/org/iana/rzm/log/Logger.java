package org.iana.rzm.log;

import org.iana.rzm.common.TrackedObject;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Logger {

    void addLog(String userName,
                String sessionID,
                String action,
                TrackedObject object,
                Object[] parameters);

    void addLog(String userName,
                String sessionID,
                String action,
                TrackedObject object);

    void addLog(String userName,
                String sessionID,
                String action,
                TrackedObject object,
                TrackedObject oldObject);
}
