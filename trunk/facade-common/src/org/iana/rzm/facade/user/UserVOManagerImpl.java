package org.iana.rzm.facade.user;

import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserManager;

/**
 * @author Piotr Tkaczyk
 */
public class UserVOManagerImpl implements UserVOManager{

    UserManager userManager;

    public UserVOManagerImpl(UserManager userManager) {
        this.userManager = userManager;
    }

    public UserVO getUserVO(long userId) throws AccessDeniedException {
        return UserConverter.convert(getRZMUser(userId));
    }

    protected RZMUser getRZMUser(long userId) throws AccessDeniedException {
        RZMUser ret = userManager.get(userId);
        if (ret == null) throw new AccessDeniedException("rzm user not found");
        if (!ret.isActive()) throw new AccessDeniedException("user " + ret.getName() + " is not active");
        return ret;
    }
}
