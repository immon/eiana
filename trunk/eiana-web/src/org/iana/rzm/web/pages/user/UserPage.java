package org.iana.rzm.web.pages.user;

import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.pages.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.services.user.*;

public abstract class UserPage extends Protected {

    @InjectObject("service:rzm.UserServices")
    public abstract UserServices getUserServices();


    @InjectPage("user/GeneralError")
    public abstract GeneralError getErrorPage();

    @InjectObject("service:rzm.UserExternalPageErrorHandler")
    public abstract ExternalPageErrorHandler getExternalPageErrorHandler();

    public RzmServices getRzmServices() {
        return getUserServices();
    }

    protected String getErrorPageName(){
        return getErrorPage().getPageName();
    }

    
}
