package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.common.admin.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.pages.user.*;
import org.iana.rzm.web.util.*;

import java.util.*;

public abstract class DomainChangesConfirmation extends AdminPage implements PageBeginRenderListener {

    @Component(id = "country", type = "Insert", bindings = {"value=prop:countryName"})
    public abstract IComponent getCountryNameComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domainName"})
    public abstract IComponent getDomainNameComponent();

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

    @Persist("client:page")
    public abstract long getDomainId();

    public abstract void setDomainId(long id);

    @Persist("client:page")
    public abstract List<ActionVOWrapper> getActionList();

    public abstract void setActionList(List<ActionVOWrapper> list);

    public abstract PageEditorListener<DomainVOWrapper> getEditor();

    @Persist("client:page")
    public abstract void setEditor(PageEditorListener<DomainVOWrapper> editor);

    @Persist("client:page")
    public abstract DomainVOWrapper getModifiedDomain();

    @Persist("client:page")
    public abstract String getCountryName();

    @Persist("client:page")
    public abstract String getBorderHeader();
    public abstract void setBorderHeader(String header);

    public abstract void setModifiedDomain(DomainVOWrapper domain);

    public abstract ActionVOWrapper getAction();

    public abstract ChangeVOWrapper getChange();

    public abstract String getDomainName();

    public abstract void setDomainName(String domainName);

    public abstract void setCountryName(String name);

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
        setModifiedDomain(getVisitState().getMmodifiedDomain());
        DomainVOWrapper currentDomain = getVisitState().getCurrentDomain(getDomainId());
        setDomainName(currentDomain.getName());
        setCountryName(getAdminServices().getCountryName(currentDomain.getName()));
        try {
            if (getActionList() == null) {
                TransactionActionsVOWrapper transactionActions = getAdminServices().getChanges(currentDomain);
                setActionList(transactionActions.getChanges());
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        } catch (AccessDeniedException e) {
            getAccessDeniedHandler().handleAccessDenied(e, UserGeneralError.PAGE_NAME);
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
        try{
            getEditor().saveEntity(getVisitState().getMmodifiedDomain(), getRequestCycle());
        }catch(NoObjectFoundException e){
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        } catch (NoDomainModificationException e) {
            setErrorMessage(getMessageUtil().getDomainModificationErrorMessage(e.getDomainName()));
        } catch (TransactionExistsException e) {
            // todo: properly handle this exception in the UI
        }
    }

    public void continueEdit() {
        getEditor().cancel(getRequestCycle());
    }


    public void resetStateIfneeded() {
        getVisitState().markAsNotVisited(getDomainId());
    }
}
