package org.iana.rzm.facade.system.domain;

import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.user.UserVO;

/**
* @author Piotr Tkaczyk
*/

public class TestAuthenticatedUser extends AuthenticatedUser {

    public TestAuthenticatedUser(UserVO user) {
        super(user.getObjId(), user.getUserName(), false);
    }

    public AuthenticatedUser getAuthUser() {
        return this;
    }
}
