package org.iana.rzm.web.components.admin;

import org.apache.commons.lang.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.admin.trans.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.pages.admin.*;
import org.iana.rzm.web.services.admin.*;

import java.util.*;

@ComponentClass
public abstract class
    AdminRequestDetails extends RequestDetails {

    @Component(id = "requestSummery", type = "AdminRequestSummery", bindings = {
        "domainName=prop:domainName", "listener=prop:listener", "request=prop:request", "confirmationSenderListener=prop:resend",
        "linkTragetPage=prop:editDomain"
        })
    public abstract IComponent getRequestSummaryComponent();

    @Component(id = "exceptionState", type = "If", bindings = {"condition=prop:exceptionState"})
    public abstract IComponent getExceptionStateComponent();

    @Component(id = "stateMessage", type = "Insert", bindings = {"value=prop:request.stateMessage"})
    public abstract IComponent getStateMessageComponent();

    @Component(id = "impactedPartiesConfirmations",
               type = "ListRequestConfirmations",
               bindings = {"confirmations=prop:impactedPartiesConfirmations"})
    public abstract IComponent getImpactedPartiesConfirmationsComponent();

    @Component(id = "displayVerisignCheck", type = "If", bindings = {"condition=prop:showPollLink"})
    public abstract IComponent getShowVerisignCheckComponent();

    @Component(id="checkVerisignState", type="DirectLink", bindings = {
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
            "listener=listener:checkVerisignState", "parameters=prop:request.rtId"
            })
    public abstract IComponent getCheckVerisignStateComponent();

    @Component(id="verisignStatus", type = "Insert", bindings = {"value=prop:verisignStatus"})
    public abstract IComponent getVerisignStatusComponent();


    @Asset(value = "WEB-INF/admin/AdminRequestDetails.html")
    public abstract IAsset get$template();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getAdminServices();

    @InjectPage("admin/EditRequest")
    public abstract EditRequest getEditRequest();

    @InjectPage(EditDomain.PAGE_NAME)
    public abstract EditDomain getEditDomain();

    @InjectPage(SendConfirmation.PAGE_NAME)
    public abstract SendConfirmation getNotificationSender();

    @Persist()
    public abstract void setNotifications(List<NotificationVOWrapper> list);
    public abstract List<NotificationVOWrapper> getNotifications();

    @Persist
    public abstract String getVerisignStatus();
    public abstract void setVerisignStatus(String status);


    public void pageBeginRender(PageEvent event) {
        super.pageBeginRender(event);

        if(!getRequest().isPartOfEPPState()){
            setVerisignStatus("Not Available");
        }else{
            setVerisignStatus(getRequest().getVerisignStatus());
        }

    }

    public List<ConfirmationVOWrapper> getImpactedPartiesConfirmations() {
        return getRequest().getImpactedPartiesConfirmations();
    }

    public boolean isEnabled() {
        return getNotifications() != null && getNotifications().size() > 0;
    }

    public boolean isExceptionState() {
        return StringUtils.isNotBlank(getRequest().getStateMessage());
    }

    protected AdminServices getRzmServices() {
        return getAdminServices();
    }

    protected String getExceptionPage() {
        return AdminGeneralError.PAGE_NAME;
    }

    public boolean isShowPollLink() {
        return getRequest().isPartOfEPPState();
    }

    public void checkVerisignState(){
        try {
            String status = getAdminServices().getVerisignStatus(getRequest().getId());
            setVerisignStatus(status);
        } catch (NoObjectFoundException e) {
            AdminPage page = (AdminPage) getPage();
            page.handleNoObjectFoundException(e);
        } catch (InvalidEPPTransactionException e) {
            AdminPage page = (AdminPage) getPage();
            page.setErrorMessage("Invalid EPP Transaction " +e.getMessage());
        }
    }

    public void editRequest() {
        EditRequest editRequest = getEditRequest();
        editRequest.setRequestId(getRequestId());
        getPage().getRequestCycle().activate(editRequest);
    }

    public IActionListener getResend() {
        if (!isEnabled()) {
            return null;
        }
        return new ResendConfirmationListener(getNotificationSender(), getRequest());

    }

    public IActionListener getListener() {
        if (isRequestClosed()) {
            return null;
        }
        return new EditRequestListener(getEditRequest(), getRequestId());
    }

    private static class EditRequestListener implements IActionListener {

        private EditRequest editRequest;
        private long requestId;

        public EditRequestListener(EditRequest editRequest, long requestId) {
            this.requestId = requestId;
            this.editRequest = editRequest;
        }

        public void actionTriggered(IComponent component, IRequestCycle cycle) {
            editRequest.setRequestId(requestId);
            cycle.activate(editRequest);
        }
    }

    private static class ResendConfirmationListener implements IActionListener {
        private SendConfirmation confirmation;
        private TransactionVOWrapper wrapper;

        public ResendConfirmationListener(SendConfirmation confirmation, TransactionVOWrapper wrapper) {
            this.confirmation = confirmation;
            this.wrapper = wrapper;
        }


        public void actionTriggered(IComponent component, IRequestCycle cycle) {
            confirmation.setRequestId(wrapper.getId());
            confirmation.setRequest(wrapper);
            cycle.activate(confirmation);
        }
    }
}
