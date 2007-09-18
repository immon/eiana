package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.pages.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.services.admin.*;

public abstract class AdminPage extends Protected {

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getAdminServices();

    public RzmServices getRzmServices(){
        return getAdminServices();
    }

    @InjectPage("admin/GeneralError")
    public abstract GeneralError getErrorPage();

    protected String getErrorPageName(){
        return getErrorPage().getPageName();
    }

    public void pageValidate(PageEvent event) {
        super.pageValidate(event);
        if(!getVisitState().getUser().isAdmin()){
            Login login = getLogin();
            login.setErrorMessage("You must be admin to view this page");
            throw new PageRedirectException(login);
        }
    }

    public void resetStateIfneeded() {

    }
}
