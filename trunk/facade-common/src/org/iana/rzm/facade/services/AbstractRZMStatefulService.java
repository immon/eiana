package org.iana.rzm.facade.services;

import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.user.UserVO;

/**
 * A helper implementation of <code>RZMStatefulService</code>. It contains
 * a placeholder for an <code>AuthenticatedUser</code> instance and provides
 * some helper checks whether a user is in given roles.
 *
 * @author Patrycja Wegrzynowicz
 */
abstract public class AbstractRZMStatefulService implements RZMStatefulService {

    protected AuthenticatedUser user;

    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }

    public void close() {
        this.user = null;
    }

    public AuthenticatedUser getAuthenticatedUser() {
        return user;
    }

    public UserVO getUser() {
        AuthenticatedUser user = getAuthenticatedUser();
        // todo: fixme - add the forward to the user service
        return user == null ? null : new UserVO(user.getUserName());
    }
}
