package org.iana.rzm.web.components.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;

@ComponentClass
public abstract class ReSendNotification extends BaseComponent {

    @Parameter
    public abstract long getRequestId();

    @Parameter
    public abstract IActionListener getNotificationSenderListener();

    @Parameter
    public abstract boolean isEnabled();

    @Asset(value = "WEB-INF/admin/ReSendNotification.html")
    public abstract IAsset get$template();
    
}
