package org.iana.rzm.facade.auth;

import org.iana.rzm.facade.user.UserVO;

/**
* @author Piotr Tkaczyk
*/

public class TestAuthenticatedUser {
    AuthenticatedUser authUser;

    public TestAuthenticatedUser(UserVO user) {
        authUser = new AuthenticatedUser(user.getObjId(), user.getUserName());
    }

    public AuthenticatedUser getAuthUser() {
        return authUser;
    }
}
