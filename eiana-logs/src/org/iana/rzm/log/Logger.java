package org.iana.rzm.log;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.common.TrackedObject;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Logger {

    void addLog(String userName,
                String sessionID,
                String action,
                TrackedObject object);
}
