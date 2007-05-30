package org.iana.rzm.web.components.admin;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Parameter;
import org.iana.rzm.web.components.Border;
import org.iana.rzm.web.pages.MessageProperty;
import org.iana.rzm.web.tapestry.MessagePropertyCallback;


public abstract class AdminBorder extends Border {

    public static final String WINDOW_TITLE = "IANA Root Zone Maintenance";

    @Component(id="navigation", type = "Navigation", bindings = {"selected=prop:selected"})
    public abstract IComponent getNavigationComponent();

    @Asset(value = "WEB-INF/admin/AdminBorder.html")
    public abstract IAsset get$template();

    @InjectPage("admin/AdminHome")
    public abstract MessageProperty getHome();

    @Parameter(required = false, defaultValue = "literal:REQUESTS" )
    public abstract String getSelected();

    protected MessagePropertyCallback getHomeCallback() {
        return new MessagePropertyCallback(getHome());
    }

    public String getWindowTitle(){
        return WINDOW_TITLE;
    }
}
