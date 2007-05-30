package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.Persist;


public abstract class RequestInformation extends AdminPage {

    public static final String PAGE_NAME = "admin/RequestInformation";

    @Component(id="requestDetails", type="AdminRequestDetails", bindings = {"requestId=prop:requestId"})
    public abstract IComponent getRequestDetailsComponent();

    @Persist("client:page")
    public abstract void setRequestId(long requestId);
    public abstract long getRequestId();


    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){
        if(parameters.length == 0){
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        setRequestId((Long)parameters[0]);
    }

    protected Object[] getExternalParameters() {
        return new Object[]{getRequestId()};
    }
}
