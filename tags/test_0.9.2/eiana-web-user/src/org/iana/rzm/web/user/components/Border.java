package org.iana.rzm.web.user.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.callback.*;
import org.iana.rzm.web.common.components.*;
import org.iana.rzm.web.user.pages.*;
import org.iana.web.tapestry.callback.*;
import org.iana.web.tapestry.feedback.*;

@ComponentClass(allowBody = true, allowInformalParameters = false)
public abstract class Border extends BaseBorder {

    public static final String WINDOW_TITLE = "IANA Root Zone Maintenance";

    @Asset(value = "WEB-INF/user/Border.html")
    public abstract IAsset get$template();

    @InjectPage(Home.PAGE_NAME)
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
