package org.iana.rzm.facade.services;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.UserVOManager;

/**
 * A helper implementation of <code>RZMStatefulService</code>. It contains
 * a placeholder for an <code>AuthenticatedUser</code> instance and provides
 * some helper checks whether a user is in given roles.
 *
 * @author Patrycja Wegrzynowicz
 */
abstract public class AbstractRZMStatefulService implements RZMStatefulService {

    protected AuthenticatedUser user;

    private UserVOManager userVOManager;

    protected AbstractRZMStatefulService(UserVOManager userManager) {
        CheckTool.checkNull(userManager,  "user manager is null");
        this.userVOManager = userManager;
    }

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
        if (user == null) throw new AccessDeniedException("no authenticated user");
        return userVOManager.getUserVO(user.getObjId());

    }
}
