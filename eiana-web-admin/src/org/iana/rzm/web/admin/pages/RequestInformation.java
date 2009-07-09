package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.web.admin.components.RequestDetails;
import org.iana.rzm.web.common.LinkTraget;
import org.iana.rzm.web.common.model.NotificationVOWrapper;

import java.util.List;


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
            return;
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
