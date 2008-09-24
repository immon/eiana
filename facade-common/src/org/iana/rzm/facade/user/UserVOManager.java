package org.iana.rzm.facade.user;

import org.iana.rzm.facade.auth.AccessDeniedException;

/**
 * @author Piotr Tkaczyk
 */
public interface UserVOManager {

    public UserVO getUserVO(long userId) throws AccessDeniedException;
}
