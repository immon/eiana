package org.iana.rzm.web.user.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.components.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.user.pages.*;
import org.iana.rzm.web.user.services.*;

import java.util.*;

@ComponentClass
public abstract class RequestDetails extends BaseRequestDetails {

    @Asset(value = "WEB-INF/user/RequestDetails.html")
    public abstract IAsset get$template();

    @Component(id = "proceed", type = "DirectLink", bindings = {
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER",
        "listener=listener:proceed",
        "parameters=prop:requestId"
        })
    public abstract IComponent getRequestDeclineComponent();

    @Component(id = "impactedPartiesConfirmations",
               type = "rzmLib:ListRequestConfirmations",
               bindings = {"confirmations=prop:impactedpartiesConfirmations"})
    public abstract IComponent getImpactedPartiesConfirmations();

    @Component(id = "technicalCkeckedfailed", type = "If", bindings = {"condition=prop:technicalCkeckedfailed"})
    public abstract IComponent getExceptionStateComponent();

    @Component(id = "stateMessage", type = "Insert", bindings = {"value=prop:request.stateMessage"})
    public abstract IComponent getStateMessageComponent();

    @InjectPage(RequestConfirmation.PAGE_NAME)
    public abstract RequestConfirmation getRequestConfirmation();

    @Asset(value = "images/spacer.png")
    public abstract IAsset getSpacer();

    @InjectObject("service:rzm.UserServices")
    public abstract UserServices getUserServices();

    @InjectState("visit")
    public abstract Visit getVisitState();

    public abstract void setUser(UserVOWrapper user);
    public abstract UserVOWrapper getUser();

    protected UserServices getRzmServices() {
        return getUserServices();
    }

    protected String getExceptionPage() {
        return GeneralError.PAGE_NAME;
    }

    public void pageBeginRender(PageEvent event) {
        super.pageBeginRender(event);
        setUser(getUserServices().getUser(getVisitState().getUserId()));
    }

    public List<ConfirmationVOWrapper> getImpactedpartiesConfirmations() {
        return getRequest().getImpactedPartiesConfirmations();
    }

    public String getSpaceerColspan() {
        return isActionEnabled() ? "6" : "5";
    }

    public boolean isTechnicalCkeckedfailed() {
        TransactionVOWrapper wrapper = getRequest();
        return wrapper.getState().equals(TransactionStateVOWrapper.State.PENDING_TECH_CHECK_REMEDY);
    }

    public boolean isActionEnabled() {
        TransactionVOWrapper wrapper = getRequest();

        boolean result = false;

        if (getUser().isTc() && (!wrapper.tcConfirmed())) {
            result = true;
        }

        if (getUser().isAc() && (!wrapper.acConfirmed())) {
            result = true;
        }

        return wrapper.getState().equals(TransactionStateVOWrapper.State.PENDING_CONTACT_CONFIRMATION) && result;

    }

    public void proceed(long requestId) {
        RequestConfirmation page = getRequestConfirmation();
        page.setRequestId(requestId);
        UserPage userPage = (UserPage) getPage();
        page.setCallback(userPage.createCallback());
        getPage().getRequestCycle().activate(page);
    }
}
