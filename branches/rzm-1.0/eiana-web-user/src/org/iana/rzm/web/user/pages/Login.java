package org.iana.rzm.web.user.pages;

import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.common.pages.*;

public abstract class Login extends BaseLogin {

    public static final String USER_COOKIE_NAME = " org.iana.rzm.web.user.pages.username";

    @Persist("client")
    public abstract void setUserLoginError(String message);
    public abstract String getUserLoginError();

    protected String getCookieName() {
        return USER_COOKIE_NAME;
    }

    public void pageBeginRender(PageEvent event) {
        super.pageBeginRender(event);
        if (getUserName() == null) {
            setUserName(getCookieSource().readCookieValue(USER_COOKIE_NAME));
        }

        if (getUserLoginError() != null) {
            setErrorMessage(getUserLoginError());
        }
        setUserLoginError(null);
    }
}
