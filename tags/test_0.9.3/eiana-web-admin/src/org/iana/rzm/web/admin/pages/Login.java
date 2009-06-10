package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.common.pages.*;

public abstract class Login extends BaseLogin {

    public static final String ADMIN_COOKIE_NAME = "org.iana.rzm.web.admin.pages.username";

    @Persist("client")
    public abstract void setAdminLoginError(String message);
    public abstract String getAdminLoginError();

    protected String getCookieName() {
        return ADMIN_COOKIE_NAME;
    }

    public void pageBeginRender(PageEvent event) {
        super.pageBeginRender(event);
        if (getUserName() == null) {
            setUserName(getCookieSource().readCookieValue(ADMIN_COOKIE_NAME));
        }

        if (getAdminLoginError() != null) {
            setErrorMessage(getAdminLoginError());
        }
        setAdminLoginError(null);
    }

}
