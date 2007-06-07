package org.iana.rzm.web.pages.user;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.model.ActionVOWrapper;
import org.iana.rzm.web.model.ChangeMessageBuilder;
import org.iana.rzm.web.model.ChangeVOWrapper;
import org.iana.rzm.web.model.TransactionVOWrapper;
import org.iana.rzm.web.util.CounterBean;

import java.util.List;

public abstract class RequestConfirmation extends UserPage implements PageBeginRenderListener {

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

    @Bean(ChangeMessageBuilder.class)
    public abstract ChangeMessageBuilder getMessageBuilder();

    @Bean(value = CounterBean.class)
    public abstract CounterBean getCounterBean();

    @InjectPage("user/UserHome")
    public abstract UserHome getHome();

    @Persist("client:page")
    public abstract void setRequestId(long requestId);

    public abstract long getRequestId();

    public abstract ActionVOWrapper getAction();

    public abstract ChangeVOWrapper getChange();

    public abstract String getToken();

    public abstract TransactionVOWrapper getRequest();

    public abstract void setRequest(TransactionVOWrapper transaction);

    public void pageBeginRender(PageEvent event) {
        try {
            TransactionVOWrapper transaction = getRzmServices().getTransaction(getRequestId());
            setRequest(transaction);
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e);
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
            getObjectNotFoundHandler().handleObjectNotFound(e);
        }
    }

    public void decline() {
        try {
                getUserServices().rejectTransaction(getRequestId(), getToken());
            getRequestCycle().activate(getHome());
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e);
        }
    }
}
