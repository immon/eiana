package org.iana.rzm.web.admin.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;

@ComponentClass
public abstract class SelectionLink extends BaseComponent {

    @Parameter(required = true)
    public abstract String getSpanStyle();

    @Parameter(required = true)
    public abstract String getlinkStyle();

    @Parameter(required = true)
    public abstract String getLinkText();

    @Parameter(required = true)
    public abstract IActionListener getListener();

    @Parameter(required = false, defaultValue = "false")
    public abstract boolean isUseDivStyle();

    @Asset(value = "WEB-INF/admin/SelectionLink.html")
    public abstract IAsset get$template();
     
}
