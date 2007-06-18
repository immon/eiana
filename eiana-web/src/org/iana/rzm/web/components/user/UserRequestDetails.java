package org.iana.rzm.web.components.user;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.web.components.RequestDetails;
import org.iana.rzm.web.model.TransactionVOWrapper;
import org.iana.rzm.web.model.UserVOWrapper;
import org.iana.rzm.web.pages.user.RequestConfirmation;
import org.iana.rzm.web.pages.user.UserGeneralError;
import org.iana.rzm.web.services.user.UserServices;

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

        return result;
    }

    public void proceed(long requestId){
        RequestConfirmation page = getRequestConfirmation();
        page.setRequestId(requestId);
        getPage().getRequestCycle().activate(page);
    }
}
