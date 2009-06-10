package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.components.admin.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.tapestry.*;

import java.util.*;


public abstract class RequestInformation extends AdminPage implements PageBeginRenderListener, IExternalPage, LinkTraget {

    public static final String PAGE_NAME = "admin/RequestInformation";

    @Component(id = "requestDetails", type = "AdminRequestDetails", bindings = {"requestId=prop:requestId"})
    public abstract IComponent getRequestDetailsComponent();

    @Persist("client:page")
    public abstract void setRequestId(long requestId);

    @InjectComponent("requestDetails")
    public abstract AdminRequestDetails getAdminRequestDetails();

    public abstract void setList(List<NotificationVOWrapper> list);
    public abstract long getRequestId();

    public void setIdentifier(Object id){
        setRequestId(Long.parseLong(id.toString()));
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length == 0) {
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }
        setRequestId((Long) parameters[0]);
    }

    public void pageBeginRender(PageEvent event) {
        List<NotificationVOWrapper> list = getAdminServices().getNotifications(getRequestId());
        getAdminRequestDetails().setNotifications(list);
    }

    protected Object[] getExternalParameters() {
        return new Object[]{getRequestId()};
    }
}
