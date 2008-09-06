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

public abstract class DomainChangesConfirmation extends AdminPage implements PageBeginRenderListener {

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
    public abstract PageEditorListener<DomainVOWrapper> getEditor();
    public abstract void setEditor(PageEditorListener<DomainVOWrapper> editor);

    @Persist("client")
    public abstract DomainVOWrapper getModifiedDomain();

    @Persist("client")
    public abstract String getCountryName();
    public abstract void setCountryName(String name);

    @Persist("client")
    public abstract String getBorderHeader();
    public abstract void setBorderHeader(String header);

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
                TransactionActionsVOWrapper transactionActions = getAdminServices().getChanges(currentDomain);
                setActionList(transactionActions.getChanges());
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        } catch (AccessDeniedException e) {
            getAccessDeniedHandler().handleAccessDenied(e, GeneralError.PAGE_NAME);
        } catch (SharedNameServersCollisionException e) {
            setErrorMessage(getMessageUtil().getSharedNameServersCollisionMessage(e.getNameServers()));
        } catch (RadicalAlterationException e) {
            setErrorMessage(getMessageUtil().getAllNameServersChangeMessage());
        }

    }

    protected Object[] getExternalParameters() {
        DomainVOWrapper domain = getModifiedDomain();
        if (domain != null) {
            return new Object[]{getDomainId(), getActionList(), domain};
        }
        return new Object[]{getDomainId(), getActionList()};
    }


    public void proceed() {
        try {
            getEditor().saveEntity(this,getVisitState().getModifiedDomain(getDomainId()), getRequestCycle());
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        } catch (NoDomainModificationException e) {
            setErrorMessage(getMessageUtil().getDomainModificationErrorMessage(e.getDomainName()));
        } catch (TransactionExistsException e) {
            // todo: properly handle this exception in the UI
        } catch (NameServerChangeNotAllowedException e) {
            // todo: proper handling of this exception
            setErrorMessage(getMessageUtil().getNameServerChangeNotAllowedErrorMessage());
        } catch (SharedNameServersCollisionException e) {
            e.printStackTrace();
        } catch (RadicalAlterationException e) {
            e.printStackTrace();
        }
    }

    public void continueEdit() {
        getEditor().cancel(getRequestCycle());
    }


    public void resetStateIfneeded() {
        getVisitState().markAsNotVisited(getDomainId());
    }
}
