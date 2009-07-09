package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.iana.rzm.facade.admin.trans.FacadeTransactionException;
import org.iana.rzm.facade.admin.trans.NoSuchStateException;
import org.iana.rzm.facade.admin.trans.StateUnreachableException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.common.model.TransactionStateVOWrapper;
import org.iana.rzm.web.common.model.TransactionVOWrapper;

import java.io.Serializable;

public abstract class EditTransactionState extends AdminPage implements PageBeginRenderListener, IExternalPage {

    public static final String PAGE_NAME = "EditTransactionState";
    public static final RequestStateSelectionModel STATES = new RequestStateSelectionModel();

    @Component(id = "editState", type = "Form", bindings = {
        "clientValidationEnabled=literal:true",
        "delegate=prop:validationDelegate"
        })
    public abstract IComponent getEditContactComponent();

    @Component(id = "states", type = "PropertySelection", bindings = {
        "displayName=literal:State:", "model=ognl:@org.iana.rzm.web.admin.pages.EditTransactionState@STATES", "value=prop:state"
        })
    public abstract IComponent getStatesComponent();

    @Component(id = "save", type = "LinkSubmit", bindings = {"action=listener:save"})
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @InjectPage(EditRequest.PAGE_NAME)
    public abstract EditRequest getEditRequestPage();

    @Persist("client")
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
            return;
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
            getObjectNotFoundHandler().handleObjectNotFound(e,GeneralError.PAGE_NAME);
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
            page.setInfoMessage(getMessageUtil().getStateChangeOKMessage());
            getRequestCycle().activate(page);
        } catch (FacadeTransactionException e) {
            setErrorMessage(e.getMessage());
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        } catch (NoSuchStateException e) {
            setErrorMessage(getMessageUtil().getInvalidStateErrorMessage(e.getStateName()));
        } catch (StateUnreachableException e) {
            setErrorMessage(getMessageUtil().getStateChangeErrorMessage(e.getStateName()));
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


