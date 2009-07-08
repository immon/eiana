package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.admin.pages.listeners.*;
import org.iana.rzm.web.common.changes.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.utils.*;

import java.util.*;

public abstract class DomainChangesConfirmation extends AdminPage implements PageBeginRenderListener, IExternalPage {

    public static final String PAGE_NAME = "DomainChangesConfirmation" ;


    @Component(id = "domainHeader", type = "rzmLib:DomainHeader", bindings = {"countryName=prop:countryName", "domainName=prop:domainName"})
    public abstract IComponent getDomainHeaderComponentComponent();

    @Component(id = "actionlist", type = "For", bindings = {"source=prop:actionList", "value=prop:action"})
    public abstract IComponent getActionForComponent();

    @Component(id = "changeList", type = "For", bindings = {"source=prop:action.changes", "value=prop:change"})
    public abstract IComponent getChangeForComponent();

    @Component(id = "title", type = "Insert", bindings = {"value=prop:changeTitle"})
    public abstract IComponent getTitleComponent();

    @Component(id = "message", type = "Insert", bindings = {"value=prop:message"})
    public abstract IComponent getMessageComponent();

    @Component(id = "continueEdit", type = "LinkSubmit", bindings = {"listener=listener:continueEdit"})
    public abstract IComponent getContinueEditComponent();

    @Component(id = "proceed", type = "LinkSubmit", bindings = {"listener=listener:proceed"})
    public abstract IComponent getProceedComponent();

    @Component(id = "div", type = "Any", bindings = {"style=prop:style"})
    public abstract IComponent getStyleComponent();

    @Component(id = "form", type = "Form")
    public abstract IComponent getFormComponent();

    @Component(id="pendingRadicalChanges", type="If", bindings = {"condition=prop:displayRadicalChangesMessage"})
    public abstract IComponent getPendingRadicalChangesComponent();

    @Bean(ChangeMessageBuilder.class)
    public abstract ChangeMessageBuilder getMessageBuilder();

    @Bean(value = CounterBean.class)
    public abstract CounterBean getCounterBean();

    @Persist("client")
    public abstract long getDomainId();
    public abstract void setDomainId(long id);

    @Persist("client")
    public abstract List<ActionVOWrapper> getActionList();
    public abstract void setActionList(List<ActionVOWrapper> list);

    @Persist("client")
    public abstract PageEditorListener<DomainVOWrapper, DomainChangesConfirmation> getEditor();
    public abstract void setEditor(PageEditorListener<DomainVOWrapper, DomainChangesConfirmation> editor);

    @Persist("client")
    public abstract DomainVOWrapper getModifiedDomain();

    @Persist("client")
    public abstract String getBorderHeader();
    public abstract void setBorderHeader(String header);

    @InitialValue("literal:false")
    @Persist("client")
    public abstract void setDisplayRadicalChangesMessage(boolean b);
    public abstract boolean isDisplayRadicalChangesMessage();

    public abstract String getCountryName();
    public abstract void setCountryName(String name);


    public abstract void setModifiedDomain(DomainVOWrapper domain);

    public abstract void setDomainName(String domainName);
    public abstract String getDomainName();

    public abstract ActionVOWrapper getAction();

    public abstract ChangeVOWrapper getChange();

    public String getChangeTitle() {
        return getAction().getTitle();
    }

    public String getStyle() {
        return "margin: 0 0 20px 60px; width: 75%;";
    }

    public String getMessage() {
        return getCounter() + ". " + getMessageBuilder().message(getChange());
    }

    public int getCounter() {
        return getCounterBean().getCounter();
    }

    public void pageBeginRender(PageEvent event) {
        setModifiedDomain(getVisitState().getModifiedDomain(getDomainId()));
        DomainVOWrapper currentDomain = getVisitState().getCurrentDomain(getDomainId());
        setDomainName(currentDomain.getName());
        setCountryName(getAdminServices().getCountryName(currentDomain.getName()));
        try {
            if (getActionList() == null) {
                TransactionActionsVOWrapper transactionActions = getAdminServices().getChanges(currentDomain, false);
                setActionList(transactionActions.getChanges());
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        } catch (AccessDeniedException e) {
            getAccessDeniedHandler().handleAccessDenied(e, GeneralError.PAGE_NAME);
        } catch (SharedNameServersCollisionException e) {
            setErrorMessage(getMessageUtil().getSharedNameServersCollisionMessage(e.getNameServers()));
        } catch (RadicalAlterationException e) {
            setErrorMessage(getMessageUtil().getRadicalAlterationCheckMessage(e.getDomainName()));
        }

    }

    protected Object[] getExternalParameters() {
        DomainVOWrapper domain = getModifiedDomain();
        if (domain != null) {
            return new Object[]{getDomainId(), getActionList(), isDisplayRadicalChangesMessage(), domain};
        }
        return new Object[]{getDomainId(), getActionList(),isDisplayRadicalChangesMessage()};
    }

    @SuppressWarnings("unchecked")
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){

        if (parameters.length < 3) {
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
        }

        setDomainId(Long.valueOf(parameters[0].toString()));
        setActionList((List<ActionVOWrapper>) parameters[1]);
        setDisplayRadicalChangesMessage(Boolean.valueOf(parameters[2].toString()));
        try {
            restoreCurrentDomain(getDomainId());
            if (parameters.length > 3 && parameters[3] != null) {
                restoreModifiedDomain((DomainVOWrapper) parameters[3]);
            }
        } catch (NoObjectFoundException e) {
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
        }
    }


    public void proceed() {
        try {
            getEditor().saveEntity(this,getVisitState().getModifiedDomain(getDomainId()), getRequestCycle(), isDisplayRadicalChangesMessage());
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        }
    }

    public void continueEdit() {
        getEditor().cancel(getRequestCycle());
    }


    public void resetStateIfneeded() {
        getVisitState().markAsNotVisited(getDomainId());
    }
}
