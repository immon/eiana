package org.iana.rzm.web.user.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.common.pages.*;
import org.iana.rzm.web.common.services.*;
import org.iana.rzm.web.user.services.*;

public abstract class UserPage extends ProtectedPage {

    @InjectObject("service:rzm.UserServices")
    public abstract UserServices getUserServices();
    
    @InjectObject("service:rzm.ExternalPageErrorHandler")
    public abstract ExternalPageErrorHandler getExternalPageErrorHandler();

    public RzmServices getRzmServices() {
        return getUserServices();
    }

    protected String getErrorPageName(){
        return getErrorPage().getPageName();
    }

    public boolean isUserPage(){
        return true;
    }


    public void pageValidate(PageEvent event) {
        super.pageValidate(event);
        if (getVisitState().getUser().isAdmin()) {
            Login login = (Login) logout();
            login.setUserLoginError(getMessageUtil().getOnlyUserErrorMessage());
            throw new PageRedirectException(login);
        }
    }
}
