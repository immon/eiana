package org.iana.rzm.web.pages.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;


public abstract class RequestInformation extends UserPage implements IExternalPage {

    public static final String PAGE_NAME = "user/RequestInformation"; 

    @Component(id="requestDetails", type="UserRequestDetails", bindings = {"requestId=prop:requestId"})
    public abstract IComponent getRequestDetailsComponent();

    @Component(id="impactedPartyDetails", type="UserImpactedpartiesRequestDetails", bindings = {"requestId=prop:requestId"})
    public abstract IComponent getThirdPartyRequestDetailsComponent();

    @Component(id="impactedThirdPartyView", type="If", bindings = {"condition=prop:impactedThirdPartyView"})
    public abstract IComponent getImpactedThirdPartyViewComponent();

    @Component(id="requestView", type="Else")
    public abstract IComponent getRequestViewComponent();

    @Component(id="back", type="DirectLink", bindings = {
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
        "listener=listener:back"
        })
    public abstract IComponent getBackComponent();

    @Persist("client")
    public abstract void setRequestId(long requestId);
    public abstract long getRequestId();

    @Persist("client")
    public abstract  boolean isImpactedThirdPartyView();
    public abstract void setImpactedThirdPartyView(boolean value);

    @Persist("client")
    public abstract ICallback getCallback();
    public abstract void setCallback(ICallback callback);


    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){
        if(parameters.length == 0){
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        setRequestId((Long)parameters[0]);
        setImpactedThirdPartyView(Boolean.valueOf(parameters[1].toString()));
        setCallback((ICallback) parameters[2]);
    }

    public void back(){
        getCallback().performCallback(getRequestCycle());
    }

    protected Object[] getExternalParameters() {
        return new Object[]{getRequestId(), isImpactedThirdPartyView(), getCallback()};
    }


}
