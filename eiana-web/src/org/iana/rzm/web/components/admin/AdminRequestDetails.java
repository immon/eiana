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
        "domainName=prop:domainName", "listener=prop:listener", "request=prop:request", "confirmationSenderListener=listener:resend"
        })
    public abstract IComponent getRequestSummaryComponent();

    @Asset(value = "WEB-INF/admin/AdminRequestDetails.html")
    public abstract IAsset get$template();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getUserServices();

    @InjectPage("admin/EditRequest")
    public abstract EditRequest getEditRequest();

    @InjectPage(SendConfirmation.PAGE_NAME)
    public abstract SendConfirmation getNotificationSender();

    public abstract void setNotifications(List<NotificationVOWrapper> list);
    public abstract List<NotificationVOWrapper> getNotifications();

    public boolean isEnabled(){
        return getNotifications() != null && getNotifications().size() > 0;
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

    public void resend(){
        SendConfirmation notificationSender = getNotificationSender();
        notificationSender.setRequestId(getRequestId());
        notificationSender.setRequest(getRequest());
        getPage().getRequestCycle().activate(notificationSender);
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
}
