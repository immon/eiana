package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.admin.components.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;

import java.util.*;


public abstract class RequestInformation extends AdminPage implements PageBeginRenderListener, IExternalPage, LinkTraget {

    public static final String PAGE_NAME = "RequestInformation";

    @Component(id = "requestDetails", type = "RequestDetails", bindings = {"requestId=prop:requestId"})
    public abstract IComponent getRequestDetailsComponent();

    @InjectComponent("requestDetails")
    public abstract RequestDetails getAdminRequestDetails();

    @Persist("client")
    public abstract void setRequestId(long requestId);

    @Persist("client")
    public abstract ICallback getCallback();
    public abstract void setCallback(ICallback callback);

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
        if(parameters[1] != null){
            setCallback((ICallback) parameters[1]);
        }
    }

    public void pageBeginRender(PageEvent event) {
        List<NotificationVOWrapper> list = getAdminServices().getNotifications(getRequestId());
        getAdminRequestDetails().setNotifications(list);
    }

    protected Object[] getExternalParameters() {
        return new Object[]{getRequestId(), getCallback()};
    }
}
