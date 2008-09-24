package org.iana.rzm.facade.services;

import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.user.UserVO;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.common.validators.CheckTool;

/**
 * A helper implementation of <code>RZMStatefulService</code>. It contains
 * a placeholder for an <code>AuthenticatedUser</code> instance and provides
 * some helper checks whether a user is in given roles.
 *
 * @author Patrycja Wegrzynowicz
 */
abstract public class AbstractRZMStatefulService implements RZMStatefulService {

    protected AuthenticatedUser user;

    private UserManager userManager;

    protected AbstractRZMStatefulService(UserManager userManager) {
        CheckTool.checkNull(userManager,  "user manager is null");
        this.userManager = userManager;
    }

    public void setUser(AuthenticatedUser user) {
        this.user = user;
    }

    public void close() {
        this.user = null;
    }

    protected RZMUser getRZMUser() throws AccessDeniedException {
        if (user == null) throw new AccessDeniedException("no authenticated user");
        RZMUser ret = userManager.get(user.getObjId());
        if (ret == null) throw new AccessDeniedException("rzm user not found");
        if (!ret.isActive()) throw new AccessDeniedException("user " + ret.getName() + " is not active");
        return ret;
    }

    public AuthenticatedUser getAuthenticatedUser() {
        return user;
    }

    public UserVO getUser() {
        return UserConverter.convert(getRZMUser());
    }
}
