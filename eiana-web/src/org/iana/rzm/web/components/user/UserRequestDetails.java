package org.iana.rzm.web.components.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.pages.user.*;
import org.iana.rzm.web.services.user.*;

import java.util.*;

@ComponentClass
public abstract class UserRequestDetails extends RequestDetails {

    @Asset(value = "WEB-INF/user/UserRequestDetails.html")
    public abstract IAsset get$template();

    @Component(id="proceed", type = "DirectLink", bindings = {
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
            "listener=listener:proceed",
            "parameters=prop:requestId"
            })
    public abstract IComponent getRequestDeclineComponent();

    @Component(id="impactedPartiesConfirmations", type="ListRequestConfirmations", bindings = {"confirmations=prop:impactedpartiesConfirmations"})
    public abstract IComponent getImpactedPartiesConfirmations();

    @InjectPage("user/RequestConfirmation")
    public abstract RequestConfirmation getRequestConfirmation();


    @InjectObject("service:rzm.UserServices")
    public abstract UserServices getUserServices();

    public abstract void setUser(UserVOWrapper user);
    public abstract UserVOWrapper getUser();

    protected UserServices getRzmServices(){
        return getUserServices();
    }

    protected String getExceptionPage() {
        return UserGeneralError.PAGE_NAME;
    }

    public void pageBeginRender(PageEvent event) {
        super.pageBeginRender(event);
        setUser(getUserServices().getUser());
    }

    public List<ConfirmationVOWrapper> getImpactedpartiesConfirmations() {
        return getRequest().getImpactedPartiesConfirmations();
    }

    public String getSpaceerColspan(){
        return isActionEnabled() ? "6": "5";
    }

    public boolean isActionEnabled(){
        TransactionVOWrapper wrapper = getRequest();

        boolean result = false;

        if(getUser().isTc() && (!wrapper.tcConfirmed())){
            result = true;
        }

        if(getUser().isAc() && (!wrapper.acConfirmed())){
            result = true;
        }

        return wrapper.getState().equals(TransactionStateVOWrapper.State.PENDING_CONTACT_CONFIRMATION) && result;

    }

    public void proceed(long requestId){
        RequestConfirmation page = getRequestConfirmation();
        page.setRequestId(requestId);
        getPage().getRequestCycle().activate(page);
    }
}
