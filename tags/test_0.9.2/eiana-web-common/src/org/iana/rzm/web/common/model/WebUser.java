package org.iana.rzm.web.common.model;

import org.iana.rzm.facade.auth.AuthenticatedUser;

import java.io.Serializable;

public class WebUser extends ValueObject implements Serializable {
    private AuthenticatedUser user;

    public WebUser(AuthenticatedUser user) {
        this.user = user;
    }

    public long getId() {
        return user.getObjId();
    }

    public String getUserName() {
        return user.getUserName();
    }

    public boolean isAdmin() {
        return user.isAdmin();
    }

    public boolean isRoot() {
        return user.isRoot();
    }

    public AuthenticatedUser getUser() {
        return user;
    }
}
