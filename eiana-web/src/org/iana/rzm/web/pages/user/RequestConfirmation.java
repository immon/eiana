package org.iana.rzm.web.pages.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.util.*;

import java.util.*;

public abstract class RequestConfirmation extends UserPage implements PageBeginRenderListener, IExternalPage {

    @Component(id = "form", type = "Form", bindings = {
            "clientValidationEnabled=literal:true",
            "delegate=prop:validationDelegate"
            })
    public abstract IComponent getFormtComponent();

    @Component(id = "actionlist", type = "For", bindings = {"source=prop:actionlist", "value=prop:action"})
    public abstract IComponent getActionForComponent();

    @Component(id = "changeList", type = "For", bindings = {"source=prop:action.changes", "value=prop:change"})
    public abstract IComponent getChangeForComponent();

    @Component(id = "message", type = "Insert", bindings = {"value=prop:message"})
    public abstract IComponent getMessageComponent();

    @Component(id = "title", type = "Insert", bindings = {"value=prop:action.title"})
    public abstract IComponent getTitleComponent();

    @Component(id = "token", type = "TextField", bindings = {"value=prop:token", "displayName=literal:Token:", "validators=validators:required"})
    public abstract IComponent getTokenComponent();

    @Component(id = "accept", type = "LinkSubmit", bindings = {"action=listener:accept"})
    public abstract IComponent getAcceptComponent();

    @Component(id = "decline", type = "LinkSubmit", bindings = {"action=listener:decline"})
    public abstract IComponent getDeclineComponent();

    @Component(id="back", type="DirectLink", bindings = {
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
        "listener=listener:back"
        })
    public abstract IComponent getBackComponent();

    @Bean(ChangeMessageBuilder.class)
    public abstract ChangeMessageBuilder getMessageBuilder();

    @Bean(value = CounterBean.class)
    public abstract CounterBean getCounterBean();

    @InjectPage("user/UserHome")
    public abstract UserHome getHome();

    @Persist("client")
    public abstract void setRequestId(long requestId);
    public abstract long getRequestId();

    @Persist("client")
    public abstract ICallback getCallback();
    public abstract void setCallback(ICallback callback);

    public abstract ActionVOWrapper getAction();
    public abstract ChangeVOWrapper getChange();

    public abstract String getToken();

    public abstract TransactionVOWrapper getRequest();
    public abstract void setRequest(TransactionVOWrapper transaction);

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){
        if(parameters.length == 0){
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        String idAsString = parameters[0].toString();
        setRequestId(Long.valueOf(idAsString));
        setCallback((ICallback) parameters[1]);
    }


    protected Object[] getExternalParameters() {
        return new Object[]{getRequestId(), getCallback()};
    }

    public void pageBeginRender(PageEvent event) {
        try {
            TransactionVOWrapper transaction = getRzmServices().getTransaction(getRequestId());
            setRequest(transaction);
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        }catch(AccessDeniedException e){
            getAccessDeniedHandler().handleAccessDenied(e, UserGeneralError.PAGE_NAME);
        }
    }

    public List<ActionVOWrapper> getActionlist() {
        return getRequest().getChanges();
    }

    public String getMessage() {
        return getCounter() + ". " + getMessageBuilder().message(getChange());
    }



    public int getCounter() {
        return getCounterBean().getCounter();
    }

    public void accept() {
        try {
            getUserServices().acceptTransaction(getRequestId(), getToken());
            getRequestCycle().activate(getHome());
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        } catch (AccessDeniedException e) {
            setErrorMessage("Invalid token or " + e.getMessage());
            //getAccessDeniedHandler().handleAccessDenied(e, UserGeneralError.PAGE_NAME);
        }

    }

    public void decline() {
        try {
            getUserServices().rejectTransaction(getRequestId(), getToken());
            getRequestCycle().activate(getHome());
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        } catch (AccessDeniedException e) {
            getAccessDeniedHandler().handleAccessDenied(e, UserGeneralError.PAGE_NAME);
        }
    }

    public void back(){
        getCallback().performCallback(getRequestCycle());
    }

}
