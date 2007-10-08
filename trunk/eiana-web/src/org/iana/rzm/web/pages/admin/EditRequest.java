package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.apache.tapestry.form.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.*;
import org.iana.rzm.web.model.*;

import java.io.*;

public abstract class EditRequest extends AdminPage implements PageBeginRenderListener, IExternalPage {

    public static final RequestStateSelectionModel STATES = new RequestStateSelectionModel();

    @Component(id = "editRequest", type = "Form", bindings = {
        "clientValidationEnabled=literal:true",
        "success=listener:save",
        "delegate=prop:validationDelegate"
        })
    public abstract IComponent getEditContactComponent();

    @Component(id = "requestSummary", type = "RequestSummery", bindings = {
        "domainName=prop:request.domainName", "request=prop:request","linkTragetPage=prop:editDomain"
        })
    public abstract IComponent getRequestSummaryComponent();

    @Component(id = "rt", type = "TextField", bindings = {
        "value=prop:request.rtId", "displayName=literal:Ticket Number:",
        "validators=validators:required", "translator=translator:number,omitZero=false"
        })
    public abstract IComponent getRTFieldComponent();

    @Component(id = "redeligation", type = "Checkbox", bindings = {
        "displayName=literal:Redeligation", "value=prop:request.redeligation"})
    public abstract IComponent getRoleComponent();

    @Component(id = "redeligationLabel", type = "FieldLabel", bindings = {"field=component:redeligation"})
    public abstract IComponent getRoleLabelComponent();

    @Component(id = "submitter", type = "TextField", bindings = {"value=prop:request.submitterEmail",
        "displayName=literal:Request Submitter Email",
        "validators=validators:email"})
    public abstract IComponent getSubmitterEmailComponent();

    @Component(id = "save", type = "LinkSubmit")
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @Component(id = "states", type = "PropertySelection", bindings = {
        "displayName=literal:State:", "model=ognl:@org.iana.rzm.web.pages.admin.EditRequest@STATES", "value=prop:request.state"
        })
    public abstract IComponent getStatesComponent();      

    @InjectPage("admin/EditDomain")
    public abstract EditDomain getEditDomain();

    @InjectPage("admin/AdminHome")
    public abstract AdminHome getHomePage();

    @InjectPage("admin/RequestInformation")
    public abstract RequestInformation getRequestInformation();

    @Persist("client:page")
    public abstract void setRequest(TransactionVOWrapper request);

    public abstract TransactionVOWrapper getRequest();

    @Persist("client:page")
    public abstract void setRequestId(long id);

    public abstract long getRequestId();

    protected Object[] getExternalParameters() {
        return new Object[]{getRequestId()};
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length == 0) {
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        setRequestId((Long) parameters[0]);
    }

    public void pageBeginRender(PageEvent event) {
        try {
            if (!event.getRequestCycle().isRewinding()) {
                TransactionVOWrapper request = getAdminServices().getTransaction(getRequestId());
                setRequest(request);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }
    }

    public void save() {
        TransactionVOWrapper transaction = getRequest();
        try {
            getAdminServices().updateTransaction(transaction);
            getRequestCycle().activate(getHomePage());
        } catch (RzmServerException e) {
            setErrorMessage(e.getMessage());
        }
    }

    public void revert() {
        RequestInformation requestInformation = getRequestInformation();
        requestInformation.setRequestId(getRequestId());
        getRequestCycle().activate(requestInformation);
    }

    private static class RequestStateSelectionModel implements IPropertySelectionModel, Serializable {
        private TransactionStateVOWrapper.State[] states;

        public RequestStateSelectionModel() {
            states = TransactionStateVOWrapper.State.values();
        }

        public int getOptionCount() {
            return states.length;
        }

        public Object getOption(int index) {
            return states[index];
        }

        public String getLabel(int index) {
            return states[index].getDisplayName();
        }

        public String getValue(int index) {
            return states[index].name();
        }

        public Object translateValue(String value) {
            return TransactionStateVOWrapper.State.valueOf(value);
        }
    }
}
