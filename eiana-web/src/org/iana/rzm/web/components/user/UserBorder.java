package org.iana.rzm.web.components.user;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.InjectPage;
import org.iana.rzm.web.components.Border;
import org.iana.rzm.web.pages.MessageProperty;
import org.iana.rzm.web.tapestry.MessagePropertyCallback;

@ComponentClass(allowBody = true, allowInformalParameters = false)
public abstract class UserBorder extends Border {

    public static final String WINDOW_TITLE = "IANA Root Zone Maintenance";

    @Asset(value = "WEB-INF/user/UserBorder.html")
    public abstract IAsset get$template();


    @InjectPage("user/UserHome")
    public abstract MessageProperty getHome();

    

    public void userAccess(){

    }

    protected MessagePropertyCallback getHomeCallback() {
        return new MessagePropertyCallback(getHome());
    }

    public String getWindowTitle(){
        return WINDOW_TITLE;
    }

}
