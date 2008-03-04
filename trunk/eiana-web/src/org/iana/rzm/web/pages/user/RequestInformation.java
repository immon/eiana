package org.iana.rzm.web.pages.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;


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

    @Component(id="back", type="OverviewLink", bindings = {
            "title=literal:Request Information", "page=prop:home", "actionTitle=literal:Back to Overview >"})
    public abstract IComponent getBackComponent();

    @InjectPage("user/UserHome")
    public abstract UserHome getHome();
    
    @Persist("client:page")
    public abstract void setRequestId(long requestId);
    public abstract long getRequestId();

    @Persist("client:page")
    public abstract  boolean isImpactedThirdPartyView();
    public abstract void setImpactedThirdPartyView(boolean value);


    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){
        if(parameters.length == 0){
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        setRequestId((Long)parameters[0]);
        setImpactedThirdPartyView(Boolean.valueOf(parameters[1].toString()));
    }

    protected Object[] getExternalParameters() {
        return new Object[]{getRequestId(), isImpactedThirdPartyView()};
    }

   
}
