package org.iana.rzm.web.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.util.*;

import java.util.*;

@ComponentClass
public abstract class RequestDetails extends BaseComponent implements PageBeginRenderListener{

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domainName"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "requestDomain", type = "Insert", bindings = {"value=prop:domainName"})
    public abstract IComponent getRequestDomainNameComponent();

    @Component(id = "country", type = "Insert", bindings = {"value=prop:country"})
    public abstract IComponent getCountryComponent();

    @Component(id = "actionlist", type = "For", bindings = {"source=prop:actionlist", "value=prop:action"})
    public abstract IComponent getActionForComponent();

    @Component(id = "changeList", type = "For", bindings = {"source=prop:action.changes", "value=prop:change"})
    public abstract IComponent getChangeForComponent();

    @Component(id = "message", type = "Insert", bindings = {"value=prop:message"})
    public abstract IComponent getMessageComponent();

    @Component(id = "title", type = "Insert", bindings = {"value=prop:action.title"})
    public abstract IComponent getTitleComponent();

    @Component(id = "rt", type = "Insert", bindings = {"value=prop:request.rtIdAsString"})
    public abstract IComponent getRtComponent();

    @Component(id = "state", type = "Insert", bindings = {"value=prop:request.currentStateAsString"})
    public abstract IComponent getStateComponent();

    @Component(id = "created", type = "Insert", bindings = {"value=prop:request.created"})
    public abstract IComponent getCreatedComponent();

    @Component(id = "modified", type = "Insert", bindings = {"value=prop:request.modified"})
    public abstract IComponent getModifiedComponent();

    @Component(id = "createdBy", type = "Insert", bindings = {"value=prop:request.createdBy"})
    public abstract IComponent getCreatedByComponent();

    @Component(id = "states", type = "For", bindings = {"source=prop:states", "value=prop:stateInfo", "element=literal:tr"})
    public abstract IComponent getForDomainComponent();

    @Component(id="stateName", type="Insert", bindings = {"value=prop:stateInfo.state"})
    public abstract IComponent getStateNameComponent();

    @Component(id="start", type="Insert", bindings = {"value=prop:stateInfo.start"})
    public abstract IComponent getStartComponent();

    @Component(id="end", type="Insert", bindings = {"value=prop:stateInfo.end"})
    public abstract IComponent getEndComponent();

    @Component(id="approved", type="Insert", bindings = {"value=prop:stateInfo.approvedBy"})
    public abstract IComponent getApprovedComponent();

    @Component(id="confirmations", type="ListRequestConfirmations", bindings = {"confirmations=prop:confirmations"})
    public abstract IComponent getConfirmationscomponent();

    @Bean(ChangeMessageBuilder.class)
    public abstract ChangeMessageBuilder getMessageBuilder();

    @Bean(value = CounterBean.class)
    public abstract CounterBean getCounterBean();

    @InjectObject("service:rzm.ObjectNotFoundHandler")
    public abstract ObjectNotFoundHandler getObjectNotFoundHandler();

    @InjectObject("service:rzm.AccessDeniedHandler")
    public abstract AccessDeniedHandler getAccessDeniedHandler();

    @Parameter(required = true)
    public abstract void setRequestId(long id);
    public abstract long getRequestId();

    @Persist("client:page")
    public abstract void setRequest(TransactionVOWrapper request);
    public abstract TransactionVOWrapper getRequest();

    public abstract ActionVOWrapper getAction();
    public abstract ChangeVOWrapper getChange();
    public abstract TransactionStateLogVOWrapper getStateInfo();

    @Persist("client")
    public abstract boolean isRequestClosed();
    public abstract void setRequestClosed(boolean value);

    public String getDomainName() {
        return getRequest().getDomainName();
    }

    public List<ActionVOWrapper> getActionlist() {
        return getRequest().getChanges();
    }

    public List<ConfirmationVOWrapper>getConfirmations(){
        return getRequest().getContactConfirmations();
    }

    public String getCountry() {
        return "(" + getRzmServices().getCountryName(getDomainName()) + ")";
    }

    public String getMessage() {
        return getCounter() + ". " + getMessageBuilder().message(getChange());
    }

    public int getCounter() {
        return getCounterBean().getCounter();
    }

    public List<TransactionStateLogVOWrapper>getStates(){
        return getRequest().getStateHistory();
    }

    public void pageBeginRender(PageEvent event){
        try {
            TransactionVOWrapper transaction = getRzmServices().getTransaction(getRequestId());
            setRequest(transaction);
            setRequestClosed(transaction.isClose());
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, getExceptionPage());
        } catch(AccessDeniedException e){
            getAccessDeniedHandler().handleAccessDenied( e, getExceptionPage());
        }
    }

    protected abstract RzmServices getRzmServices();

    protected abstract String getExceptionPage();
}
    

