package org.iana.rzm.log;

import org.iana.rzm.user.RZMUser;
import org.iana.rzm.common.TrackedObject;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Logger {

    void addLog(RZMUser user,
                String action,
                TrackedObject object);
}
