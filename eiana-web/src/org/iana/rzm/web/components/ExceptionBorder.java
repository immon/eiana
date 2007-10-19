package org.iana.rzm.web.components;

import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.pages.*;
import org.iana.rzm.web.pages.admin.*;
import org.iana.rzm.web.pages.user.*;
import org.iana.rzm.web.tapestry.*;

public abstract class ExceptionBorder extends Border {
    public static final String WINDOW_TITLE = "IANA Root Zone Maintenance";
        
    @InjectPage(AdminHome.PAGE_NAME)
    public abstract AdminPage getAdminHome();

    @InjectPage(UserHome.PAGE_NAME)
    public abstract UserHome getUserHome();

    protected MessageProperty getHome(){
        if(isLoggedIn()){
            if(getVisit().getUser().isAdmin()){
                return getAdminHome();
            }

            return getUserHome();
        }
        return getLogin();
    }

    protected MessagePropertyCallback getHomeCallback() {
        return new MessagePropertyCallback(getHome());
    }

    public String getWindowTitle() {
        return WINDOW_TITLE;
    }
}
