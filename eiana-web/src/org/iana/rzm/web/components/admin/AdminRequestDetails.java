package org.iana.rzm.web.components.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.pages.admin.*;
import org.iana.rzm.web.services.admin.*;

import java.util.*;

@ComponentClass
public abstract class AdminRequestDetails extends RequestDetails  {

    @Component(id = "requestSummery", type = "RequestSummery", bindings = {
        "domainName=prop:domainName", "listener=prop:listener", "request=prop:request", "confirmationSenderListener=prop:resend",
        "linkTragetPage=prop:editDomain"
        })
    public abstract IComponent getRequestSummaryComponent();

    @Component(id="exceptionState", type="If", bindings = {"condition=prop:exceptionState"})
    public abstract IComponent getExceptionStateComponent();

    @Component(id="stateMessage", type = "Insert", bindings = {"value=prop:request.stateMessage"})
    public abstract IComponent getStateMessageComponent();

     @Component(id="impactedPartiesConfirmations", type="ListRequestConfirmations", bindings = {"confirmations=prop:impactedPartiesConfirmations"})
    public abstract IComponent getImpactedPartiesConfirmationsComponent();

    @Asset(value = "WEB-INF/admin/AdminRequestDetails.html")
    public abstract IAsset get$template();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getUserServices();

    @InjectPage("admin/EditRequest")
    public abstract EditRequest getEditRequest();

    @InjectPage(EditDomain.PAGE_NAME)
    public abstract EditDomain getEditDomain();

    @InjectPage(SendConfirmation.PAGE_NAME)
    public abstract SendConfirmation getNotificationSender();

    @Persist("client:page")
    public abstract void setNotifications(List<NotificationVOWrapper> list);
    public abstract List<NotificationVOWrapper> getNotifications();

    public List<ConfirmationVOWrapper>getImpactedPartiesConfirmations(){
        return getRequest().getImpactedPartiesConfirmations();
    }

    public boolean isEnabled(){
        return getNotifications() != null && getNotifications().size() > 0;
    }

    public boolean isExceptionState(){
        return getRequest().isException();
    }

    protected AdminServices getRzmServices() {
        return getUserServices();
    }

    protected String getExceptionPage() {
        return AdminGeneralError.PAGE_NAME;
    }

    public void editRequest() {
        EditRequest editRequest = getEditRequest();
        editRequest.setRequestId(getRequestId());
        getPage().getRequestCycle().activate(editRequest);
    }

    public IActionListener getResend(){
        if(!isEnabled()){
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

        public EditRequestListener(EditRequest editRequest, long requestId){
            this.requestId = requestId;
            this.editRequest = editRequest;
        }

        public void actionTriggered(IComponent component, IRequestCycle cycle) {
            editRequest.setRequestId(requestId);
            cycle.activate(editRequest);
        }
    }

    private static class ResendConfirmationListener implements IActionListener{
        private SendConfirmation confirmation;
        private TransactionVOWrapper wrapper;

        public ResendConfirmationListener(SendConfirmation confirmation, TransactionVOWrapper wrapper){
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
