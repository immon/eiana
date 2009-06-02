package org.iana.rzm.web.common.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.system.trans.TransactionCannotBeWithdrawnException;
import org.iana.rzm.web.common.Visit;
import org.iana.rzm.web.common.changes.ChangeMessageBuilder;
import org.iana.rzm.web.common.model.ActionVOWrapper;
import org.iana.rzm.web.common.model.ChangeVOWrapper;
import org.iana.rzm.web.common.model.SystemDomainVOWrapper;
import org.iana.rzm.web.common.model.TransactionVOWrapper;
import org.iana.rzm.web.common.pages.RzmPage;
import org.iana.rzm.web.common.services.AccessDeniedHandler;
import org.iana.rzm.web.common.services.ObjectNotFoundHandler;
import org.iana.rzm.web.common.services.RzmServices;
import org.iana.rzm.web.common.utils.CounterBean;
import org.iana.web.tapestry.callback.MessagePropertyCallback;

import java.util.List;


public abstract class BaseWithdrawRequestConfirmation extends BaseComponent implements PageBeginRenderListener {

    @Component(id = "domainHeader", type = "rzmLib:DomainHeader", bindings = {"countryName=prop:country", "domainName=prop:domainName"})
    public abstract IComponent getDomainHeaderComponentComponent();

    @Component(id = "delete", type = "DirectLink", bindings = {"listener=listener:delete",
        "parameters=prop:requestId", "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getSubmitComponent();

    @Component(id = "revert", type = "DirectLink", bindings = {"listener=listener:revert",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @Component(id = "actionlist", type = "For", bindings = {"source=prop:actionlist", "value=prop:action"})
    public abstract IComponent getActionForComponent();

    @Component(id = "changeList", type = "For", bindings = {"source=prop:action.changes", "value=prop:change"})
    public abstract IComponent getChangeForComponent();

    @Component(id = "message", type = "Insert", bindings = {"value=prop:message"})
    public abstract IComponent getMessageComponent();

    @Component(id = "title", type = "Insert", bindings = {"value=prop:action.title"})
    public abstract IComponent getTitleComponent();

    @Component(id = "comment", type = "TextArea", bindings = {"value=prop:comment"})
    public abstract IComponent getCommentComponent();

    @InjectObject("service:rzm.ObjectNotFoundHandler")
    public abstract ObjectNotFoundHandler getObjectNotFoundHandler();

    @InjectObject("service:rzm.AccessDeniedHandler")
    public abstract AccessDeniedHandler getAccessDeniedHandler();

    @Bean(ChangeMessageBuilder.class)
    public abstract ChangeMessageBuilder getMessageBuilder();

    @Bean(value = CounterBean.class)
    public abstract CounterBean getCounterBean();

    @Parameter
    public abstract void setRequestId(long id);
    public abstract long getRequestId();

    @Persist("client")
    public abstract void setRequest(TransactionVOWrapper request);

    @InjectState("visit")
    public abstract Visit getVisit();

    public abstract TransactionVOWrapper getRequest();
    public abstract ActionVOWrapper getAction();
    public abstract ChangeVOWrapper getChange();

     public String getCountry() {
        return getRzmServices().getCountryName(getRequest().getDomainName());
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

    public String getDomainName(){
        return getRequest().getDomainName();
    }

    public String getComment(){
        return null;
    }

    public void setComment(String comment){
        
    }

    public void pageBeginRender(PageEvent event){
        try {
            TransactionVOWrapper transaction = getRzmServices().getTransaction(getRequestId());
            setRequest(transaction);
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, getExceptionPage());
        } catch (AccessDeniedException e) {
            getAccessDeniedHandler().handleAccessDenied(e, getExceptionPage());
        }
    }

    protected abstract RzmServices getRzmServices();

    protected abstract String getExceptionPage();

    protected abstract MessagePropertyCallback getCallback();

    public void revert() {
        getCallback().performCallback(getPage().getRequestCycle());
    }

    protected RzmPage getRzmPage() {
        return (RzmPage) getPage();                        
    }

    public void delete(long requestId) {
        try {
            SystemDomainVOWrapper domain = getRzmServices().getDomain(getRequest().getDomainName());
            getRzmServices().withdrawnTransaction(requestId);
            getVisit().markAsNotVisited(domain.getId());
            MessagePropertyCallback callback = getCallback();
            callback.setInfoMessage(getRzmPage().getMessageUtil().getRequestWithrowOKMessage());
            callback.performCallback(getPage().getRequestCycle());
        } catch (NoObjectFoundException e) {
            getRzmPage().setErrorMessage(getRzmPage().getMessageUtil().getRequestNotFoundErrorMessage());
        } catch (TransactionCannotBeWithdrawnException e) {
            getRzmPage().setErrorMessage(
               getRzmPage().getMessageUtil().getTransactiomCannotBeWithdrawnErrorMessage());
        } catch (InfrastructureException e) {
            getRzmPage().setErrorMessage(
                getRzmPage().getMessageUtil().getTransactiomCannotBeWithdrawnErrorMessage());
        }
    }
}


    

