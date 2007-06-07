package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.RzmServerException;
import org.iana.rzm.web.model.TransactionStateVOWrapper;
import org.iana.rzm.web.model.TransactionVOWrapper;

import java.io.Serializable;

public abstract class EditRequest extends AdminPage implements PageBeginRenderListener, IExternalPage {

    public static final RequestStateSelectionModel STATES = new RequestStateSelectionModel();

    @Component(id="requestSummary", type = "RequestSummery", bindings = {
            "domainName=prop:domainName", "request=prop:request"
    })
    public abstract IComponent getRequestSummaryComponent();

    @Component(id="rt", type="TextField", bindings = {"value=prop:rt", "displayName=literal:Ticket Number:",
            "validators=validators:required", "translator=translator:number"})
    public abstract IComponent getRTFieldComponent();

    @Component(id = "editRequest", type = "Form", bindings = {
                "clientValidationEnabled=literal:true",
                "success=listener:save",
                //"cancel=listener:revert",
                "delegate=prop:validationDelegate"
                })
    public abstract IComponent getEditContactComponent();

    @Component(id = "save", type = "LinkSubmit")
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @Component(id="states", type="PropertySelection", bindings = {
            "displayName=literal:State:","model=ognl:@org.iana.rzm.web.pages.admin.EditRequest@STATES", "value=prop:state"
            })
    public abstract IComponent getStatesComponent();

    @Component(id = "redeligation", type = "Checkbox", bindings = {
            "displayName=literal:Redeligation", "value=prop:redeligation"})
    public abstract IComponent getRoleComponent();

    @Component(id = "redeligationLabel", type = "FieldLabel", bindings = {"field=component:redeligation"})
    public abstract IComponent getRoleLabelComponent();

    @InjectPage("admin/AdminHome")
    public abstract AdminHome getHomePage();


    @Persist("client:page")
    public abstract void setRequest(TransactionVOWrapper request);
    public abstract TransactionVOWrapper getRequest();

    @Persist("client:page")
    public abstract void setRequestId(long id);
    public abstract long getRequestId();

    public abstract void setDomainName(String domainName);

    public abstract void setRt(long rtId);
    public abstract long getRt();

    public abstract void setState(TransactionStateVOWrapper.State state);
    public abstract TransactionStateVOWrapper.State getState();

    public abstract void setRedeligation(boolean value);
    public abstract boolean isRedeligation();

    public abstract String getSubmitterEmail();
    public abstract void setSubmitterEmail(String email);

    protected Object[] getExternalParameters() {
        return new Object[]{getRequestId()};
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){
        if(parameters.length == 0){
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        setRequestId((Long)parameters[0]);
    }

    public void pageBeginRender(PageEvent event){
        try {
            if(!event.getRequestCycle().isRewinding()){
                TransactionVOWrapper request = getAdminServices().getTransaction(getRequestId());
                setRequest(request);
                setRt(request.getRtId());
                setState(request.getState());
                setDomainName(request.getDomainName());
                setSubmitterEmail(request.getSubmitterEmail());
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e);
        }
    }

    public void save(){
        TransactionVOWrapper transaction = getRequest();
        transaction.setRt(getRt());
        transaction.setState(getState());
        transaction.setRedeligation(isRedeligation());
        try {
            getAdminServices().updateTransaction(transaction);
            getRequestCycle().activate(getHomePage());
        } catch (RzmServerException e) {
            setErrorMessage(e.getMessage());
        }
    }

    private static class RequestStateSelectionModel implements IPropertySelectionModel, Serializable{
        private TransactionStateVOWrapper.State[] states;

        public RequestStateSelectionModel(){
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
