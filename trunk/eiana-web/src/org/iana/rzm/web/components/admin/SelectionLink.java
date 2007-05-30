package org.iana.rzm.web.components.admin;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IActionListener;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.Parameter;

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
