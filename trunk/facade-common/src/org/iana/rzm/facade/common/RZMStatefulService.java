package org.iana.rzm.facade.common;

import org.iana.rzm.facade.auth.AuthenticatedUser;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface RZMStatefulService {

    void setUser(AuthenticatedUser user);
}
