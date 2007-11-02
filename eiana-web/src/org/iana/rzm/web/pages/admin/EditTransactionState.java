package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.apache.tapestry.form.*;
import org.iana.rzm.facade.admin.trans.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;

import java.io.*;

public abstract class EditTransactionState extends AdminPage implements PageBeginRenderListener, IExternalPage {

    public static final RequestStateSelectionModel STATES = new RequestStateSelectionModel();
    public static final String PAGE_NAME = "admin/EditTransactionState";

    @Component(id = "editState", type = "Form", bindings = {
        "clientValidationEnabled=literal:true",
        "delegate=prop:validationDelegate"
        })
    public abstract IComponent getEditContactComponent();

    @Component(id = "states", type = "PropertySelection", bindings = {
        "displayName=literal:State:", "model=ognl:@org.iana.rzm.web.pages.admin.EditTransactionState@STATES", "value=prop:state"
        })
    public abstract IComponent getStatesComponent();

    @Component(id = "save", type = "LinkSubmit", bindings = {"action=listener:save"})
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @InjectPage(EditRequest.PAGE_NAME)
    public abstract EditRequest getEditRequestPage();

    @Persist("client:page")
    public abstract void setRequestId(long requestId);

    public abstract long getRequestId();

    public abstract void setState(TransactionStateVOWrapper.State state);

    public abstract TransactionStateVOWrapper.State getState();

    protected Object[] getExternalParameters() {
        return new Object[]{getRequestId(), getState()};
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length == 0) {
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        setRequestId((Long) parameters[0]);
        if (parameters.length > 1 && parameters[1] != null) {
            setState((TransactionStateVOWrapper.State) parameters[1]);
        }
    }

    public void pageBeginRender(PageEvent event) {
        try {
            if (!event.getRequestCycle().isRewinding()) {
                TransactionVOWrapper request = getAdminServices().getTransaction(getRequestId());
                if (getState() == null) {
                    setState(request.getState());
                }
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }
    }

    public void save() {
        try {
            TransactionVOWrapper request = getAdminServices().getTransaction(getRequestId());
            if(request.getState().equals(getState())){
                revert();
                return;
            }

            getAdminServices().transitTransactionToState(getRequestId(), getState());
            EditRequest page = getEditRequestPage();
            page.setRequestId(getRequestId());
            page.setInfoMessage("Transaction State change successfully");
            getRequestCycle().activate(page);
        } catch (FacadeTransactionException e) {
            setErrorMessage(e.getMessage());
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        } catch (NoSuchStateException e) {
            setErrorMessage("Invalid State " + e.getStateName());
        } catch (StateUnreachableException e) {
            setErrorMessage("You can not change the state to " + e.getStateName());
        }

    }

    public void revert() {
        EditRequest page = getEditRequestPage();
        page.setRequestId(getRequestId());
        getRequestCycle().activate(page);
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


