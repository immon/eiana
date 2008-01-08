package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.apache.tapestry.form.*;
import org.iana.rzm.facade.admin.trans.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.tapestry.*;

import java.io.*;
import java.util.*;

public abstract class SendConfirmation extends AdminPage implements PageBeginRenderListener, IExternalPage {

    public final static String PAGE_NAME = "admin/SendConfirmation";

    @Component(id = "sendConfirmation", type = "Form", bindings = {
        "clientValidationEnabled=literal:false"
        })
    public abstract IComponent getSendConfirmationComponent();

    @Component(id = "text", type = "TextArea", bindings = {
        "displayName=literal:Comment:", "value=prop:text"})
    public abstract IComponent getTextComponent();

    @Component(id = "email", type = "TextField", bindings = {
        "displayName=literal:Email", "value=prop:email", "validators=validators:email"})
    public abstract IComponent getEmailComponent();

    @Component(id = "notificationTypes", type = "PropertySelection", bindings = {
        "displayName=literal:Confirmations:", "model=prop:model", "value=prop:confirmation"
        })
    public abstract IComponent getStatesComponent();

    @Component(id = "send", type = "LinkSubmit", bindings = {"listener=listener:send"})
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "LinkSubmit", bindings = {"listener=listener:revert"})
    public abstract IComponent getCancelComponent();

    @Component(id = "requestSummery", type = "RequestSummery", bindings = {
        "domainName=prop:request.domainName", "request=prop:request", "linkTragetPage=prop:linkTarget"})
    public abstract IComponent getRequestSummaryComponent();

    @InjectPage(EditDomain.PAGE_NAME)
    public abstract EditDomain getEditDomain();

    @InjectPage("admin/RequestInformation")
    public abstract RequestInformation getRequestInformationPage();

    @Persist("client:page")
    public abstract long getRequestId();

    public abstract void setRequestId(long id);

    public abstract TransactionVOWrapper getRequest();

    public abstract void setRequest(TransactionVOWrapper wrapper);

    public abstract NotificationVOWrapper getConfirmation();

    public abstract String getText();
    public abstract void setText(String text);

    public abstract String getEmail();
    public abstract void setEmail(String email);

    public abstract void setNotifications(List<NotificationVOWrapper> notifications);

    public abstract List<NotificationVOWrapper> getNotifications();


    protected Object[] getExternalParameters() {
        return new Object[]{
            getRequestId(),getText(),getEmail()
        };
    }

    public LinkTraget getLinkTarget(){
        return getEditDomain();
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){

        if(parameters.length == 0 || parameters[0] == null){
            getExternalPageErrorHandler().handleExternalPageError(
                    getMessageUtil().getSessionRestorefailedMessage());
        }

        Long requestId = (Long) parameters[0];
        setRequestId(requestId);

        if(parameters.length >= 1 && parameters[1] != null){
            setText(parameters[1].toString());
        }

        if(parameters.length >= 2 && parameters[2] != null){
            setEmail(parameters[2].toString());
        }

    }

    public void pageBeginRender(PageEvent event) {
        List<NotificationVOWrapper> notifications = getAdminServices().getNotifications(getRequestId());
        setNotifications(notifications);
        if (getRequest() == null) {
            try {
                setRequest(getAdminServices().getTransaction(getRequestId()));
            } catch (NoObjectFoundException e) {
                getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
            }
        }

    }

    public IPropertySelectionModel getModel() {
        return new NotificationTypes(getNotifications().toArray(new NotificationVOWrapper[0]));
    }

    public void send() {
        NotificationVOWrapper notification = getConfirmation();
        String comment = getText();
        try {
            getAdminServices().sendNotification(getRequestId(), notification, comment, getEmail());
            RequestInformation page = getRequestInformationPage();
            page.setRequestId(getRequestId());
            page.setInfoMessage("The Notification was sent successfully");
            getRequestCycle().activate(page);
        } catch (FacadeTransactionException e) {
            setErrorMessage(e.getMessage());
        }
    }

    public void revert() {
        returnToRequestPage();
    }

    private void returnToRequestPage() {
        RequestInformation page = getRequestInformationPage();
        page.setRequestId(getRequestId());
        getRequestCycle().activate(page);
    }

    private static class NotificationTypes implements IPropertySelectionModel, Serializable {

        private NotificationVOWrapper[] types;

        public NotificationTypes(NotificationVOWrapper[] types) {
            this.types = types;
        }

        public int getOptionCount() {
            return types.length;
        }

        public Object getOption(int index) {
            return types[index];
        }

        public String getLabel(int index) {
            return types[index].getType().getDisplayName();
        }

        public String getValue(int index) {
            return String.valueOf(index);
        }

        public Object translateValue(String value) {
            int i = Integer.parseInt(value);
            return types[i];
        }
    }
}
