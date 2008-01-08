package org.iana.rzm.web.components.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.pages.*;
import org.iana.rzm.web.pages.user.*;
import org.iana.rzm.web.tapestry.*;

@ComponentClass(allowBody = true, allowInformalParameters = false)
public abstract class UserBorder extends Border {

    public static final String WINDOW_TITLE = "IANA Root Zone Maintenance";

    @Asset(value = "WEB-INF/user/UserBorder.html")
    public abstract IAsset get$template();


    @InjectPage("user/UserHome")
    public abstract MessageProperty getHome();


    @InjectPage(UserPasswordChange.PAGE_NAME)
    public abstract UserPasswordChange getPasswordChangePage();

    public void changePassword() {
        UserPasswordChange myPasswordChange = getPasswordChangePage();
        UserPage current = (UserPage) getPage();
        RzmCallback callback = current.createCallback();
        myPasswordChange.setCallBack(callback);
        getPage().getRequestCycle().activate(myPasswordChange);
    }

    protected MessagePropertyCallback getHomeCallback() {
        return new MessagePropertyCallback(getHome());
    }



    public String getWindowTitle(){
        return WINDOW_TITLE;
    }

}
