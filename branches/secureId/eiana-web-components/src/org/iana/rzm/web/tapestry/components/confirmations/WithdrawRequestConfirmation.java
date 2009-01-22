package org.iana.rzm.web.tapestry.components.confirmations;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.components.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.common.exceptions.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.common.changes.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.pages.*;
import org.iana.rzm.web.common.utils.*;
import org.iana.web.tapestry.callback.*;

import java.util.*;


public abstract class WithdrawRequestConfirmation extends BaseComponent implements PageBeginRenderListener {

    @Component(id="requestBlock", type="RenderBlock", bindings = {"block=prop:requestBlock"})
    public abstract IComponent getRequestBlockComponent();

    //@Component(id = "domainName", type = "Insert", bindings = {"value=prop:domainName"})
    //public abstract IComponent getDomainNameComponent();
    //
    //@Component(id = "country", type = "Insert", bindings = {"value=prop:country"})
    //public abstract IComponent getCountryComponent();

    @Component(id = "domainHeader",
               type = "rzmLib:DomainHeader",
               bindings = {"countryName=prop:country", "domainName=prop:domainName"})
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

    @Bean(ChangeMessageBuilder.class)
    public abstract ChangeMessageBuilder getMessageBuilder();

    @Bean(value = CounterBean.class)
    public abstract CounterBean getCounterBean();

    @Parameter(required = true)
    public abstract void setRequestId(long id);
    public abstract long getRequestId();

    @Parameter(required = true)
    public abstract Block getRequestBlock();

    @Parameter(required = true)
    public abstract MessagePropertyCallback getCallback();

    @Persist("client")
    public abstract void setRequest(TransactionVOWrapper request);

    public abstract TransactionVOWrapper getRequest();
    public abstract ActionVOWrapper getAction();

    public abstract ChangeVOWrapper getChange();

    public String getCountry() {
        return getRzmPage().getRzmServices().getCountryName(getRequest().getDomainName());
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

    public String getDomainName() {
        return getRequest().getDomainName();
    }

    public void pageBeginRender(PageEvent event) {
        try {
            TransactionVOWrapper transaction = getRzmPage().getRzmServices().getTransaction(getRequestId());
            setRequest(transaction);
        } catch (NoObjectFoundException e) {
            getRzmPage().getObjectNotFoundHandler().handleObjectNotFound(e,getRzmPage().getErrorPage().getPageName());
        } catch (AccessDeniedException e) {
            getRzmPage().getAccessDeniedHandler().handleAccessDenied(e,getRzmPage().getErrorPage().getPageName());
        }
    }

    protected  String getExceptionPage(){
        return getRzmPage().getErrorPage().getPageName(); 
    }



    public void revert() {
        getCallback().performCallback(getPage().getRequestCycle());
    }

    protected ProtectedPage getRzmPage() {
        return (ProtectedPage) getPage();
    }

    public void delete(long requestId) {
        try {
            SystemDomainVOWrapper domain = getRzmPage().getRzmServices().getDomain(getRequest().getDomainName());
            getRzmPage().getRzmServices().withdrawnTransaction(requestId);
            getRzmPage().getVisitState().markAsNotVisited(domain.getId());
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


    

