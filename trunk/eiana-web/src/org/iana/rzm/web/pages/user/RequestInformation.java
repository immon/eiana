package org.iana.rzm.web.pages.user;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.Persist;


public abstract class RequestInformation extends UserPage implements IExternalPage {

    public static final String PAGE_NAME = "user/RequestInformation"; 

    @Component(id="requestDetails", type="UserRequestDetails", bindings = {"requestId=prop:requestId"})
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
