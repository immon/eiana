package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.admin.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;

public abstract class EditRequest extends AdminPage implements PageBeginRenderListener, IExternalPage, LinkTraget {

    public static final String PAGE_NAME = "EditRequest";

    @Component(id = "editRequest", type = "Form", bindings = {
        "clientValidationEnabled=literal:true",
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

    @Component(id = "save", type = "LinkSubmit", bindings = {"action=listener:save"})
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @Component(id="nextState", type="LinkSubmit", bindings = {"action=listener:nextState"})
    public abstract IComponent getNextStateComponent();

    @Component(id="chooseState", type = "LinkSubmit", bindings = {"action=listener:chooseState"})
    public abstract IComponent getChooseStateComponent();

    @Component(id="docApproved", type="LinkSubmit", bindings = {"action=listener:docApproved"})
    public abstract IComponent getDocApprovedComponent();

    @Component(id="docRegected", type = "LinkSubmit", bindings = {"action=listener:docRejected"})
    public abstract IComponent getDocRejectedComponent();

    @Component(id="pendingDoc", type="If", bindings = {"condition=prop:pendingDoc" ,"element=literal:tr"})
    public abstract IComponent getIsPendingDocComponent();

    @Component(id="docNote", type = "TextArea", bindings = {"displayName=literal:DoC Note", "value=prop:request.docNote"})
    public abstract IComponent  getDocNoteComponent();

    @InjectPage(EditDomain.PAGE_NAME)
    public abstract EditDomain getEditDomain();

    @InjectPage(Home.PAGE_NAME)
    public abstract Home getHomePage();

    @InjectPage(EditTransactionState.PAGE_NAME)
    public abstract EditTransactionState getEditTransactionState();

    @InjectPage(RequestInformation.PAGE_NAME)
    public abstract RequestInformation getRequestInformation();

    @Persist("client")
    public abstract void setCallback(ICallback callback);
    public abstract ICallback getCallback();

    @Persist("client")
    public abstract void setRequest(TransactionVOWrapper request);
    public abstract TransactionVOWrapper getRequest();

    @Persist("client")
    public abstract void setRequestId(long id);
    public abstract long getRequestId();

    public void setIdentifier(Object id){
        setRequestId(Long.parseLong(id.toString()));
    }

    protected Object[] getExternalParameters() {
        return new Object[]{getRequestId(), getCallback()};
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length == 0) {
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        setRequestId((Long) parameters[0]);
        if(parameters.length == 2 && parameters[1] != null){
            setCallback((ICallback) parameters[1]);
        }
    }

    public void pageBeginRender(PageEvent event) {
        try {
            if (!event.getRequestCycle().isRewinding()) {
                TransactionVOWrapper request = getAdminServices().getTransaction(getRequestId());
                setRequest(request);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
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

    public void nextState(){
        try {
            getAdminServices().moveTransactionNextState(getRequestId());
            setInfoMessage(getMessageUtil().getStateChangeOKMessage());
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        } catch (IllegalTransactionStateException e) {
            setErrorMessage(getMessageUtil().getStateChangeErrorMessage(e.getState()) );
        }
    }

    public void chooseState(){
        EditTransactionState page = getEditTransactionState();
        page.setRequestId(getRequestId());
        getRequestCycle().activate(page);
    }

    public void docApproved(){
        try {
            getAdminServices().approveByUSDoC(getRequestId());
            setInfoMessage(getMessageUtil().getStateChangeOKMessage());
        } catch (NoObjectFoundException e) {
           getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        } catch (IllegalTransactionStateException e) {
            setErrorMessage(getMessageUtil().getStateChangeErrorMessage(e.getState()) );
        }
    }

    public void docRejected(){
        try {
            getAdminServices().rejectByUSDoC(getRequestId());
            setInfoMessage(getMessageUtil().getStateChangeOKMessage());
        } catch (NoObjectFoundException e) {
           getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        } catch (IllegalTransactionStateException e) {
            setErrorMessage(getMessageUtil().getStateChangeErrorMessage(e.getState()));
        }            
    }


    public boolean isPendingDoc(){
        return getRequest().getState().equals(TransactionStateVOWrapper.State.PENDING_USDOC_APPROVAL);
    }




}
