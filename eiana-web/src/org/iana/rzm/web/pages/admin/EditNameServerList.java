package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.event.*;
import org.apache.tapestry.form.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.tapestry.*;
import org.iana.rzm.web.util.*;

import java.util.*;

public abstract class EditNameServerList extends AdminPage
    implements PageBeginRenderListener, NameServerAttributesEditor, IExternalPage {

    public static final String PAGE_NAME = "admin/EditNameServerList";

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:isRequestPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "ShowPendingRequestsMessage",
               bindings = {"listener=listener:viewPendingRequests", "pendigRequestMessage=literal:There is Request Pending for this domain."}
    )
    public abstract IComponent getPendingRequestsMessageComponent();

    @Component(id = "editor", type = "NameServerListEditor", bindings = {
        "editor=prop:editor", "list=prop:nameServerListValue", "domainId=prop:domainId"})
    public abstract IComponent getEditorComponent();

    @Bean
    public abstract JavaScriptDelegator getDeligate();

    @InjectPage("admin/EditDomain")
    public abstract EditDomain getEditDomain();

    @InjectPage("admin/RequestsPerspective")
    public abstract RequestsPerspective getRequestsPerspective();

    @Persist("client")
    public abstract List<NameServerValue> getNameServerListValue();

    public abstract void setNameServerListValue(List<NameServerValue> list);

    @Persist("client")
    public abstract void setCallback(ICallback callback);

    public abstract ICallback getCallback();

    @Persist("client")
    public abstract long getDomainId();

    public abstract void setDomainId(long domainId);

    @Persist("client:page")
    public abstract DomainVOWrapper getModifiedDomain();

    public abstract void setModifiedDomain(DomainVOWrapper domain);

    public List<NameServerValue> getCurrentNameServers() {
        return WebUtil.convert(getVisitState().getCurrentDomain(getDomainId()).getNameServers());
    }

    public NameServerAttributesEditor getEditor() {
        return this;
    }


    protected Object[] getExternalParameters() {
        return new Object[]{
            getDomainId(), getNameServerListValue(), getCallback(), getModifiedDomain()
        };
    }

    @SuppressWarnings("unchecked")
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length < 3) {
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
        }
        Long domainId = (Long) parameters[0];
        setDomainId(domainId);
        setNameServerListValue((List<NameServerValue>) parameters[1]);
        setCallback((ICallback) parameters[2]);
        try {
            restoreCurrentDomain(getDomainId());
            if (parameters.length > 3 && parameters[3] != null) {
                restoreModifiedDomain((DomainVOWrapper) parameters[3]);
            }

        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }
    }

    public void pageBeginRender(PageEvent event) {
        setModifiedDomain(getVisitState().getModifiedDomain(getDomainId()));
        if (getNameServerListValue() == null) {
            List<NameServerValue> list = getNameServersForEdit();
            setNameServerListValue(list);
        }
    }

    private List<NameServerValue> getNameServersForEdit() {
        List<NameServerVOWrapper> nameServers = getVisitState().getCurrentDomain(getDomainId()).getNameServers();
        List<NameServerValue> list = new ArrayList<NameServerValue>();
        for (NameServerVOWrapper wrapper : nameServers) {
            list.add(new NameServerValue(wrapper));
        }
        return list;
    }

    public void save(List<NameServerValue> list) {
        List<NameServerVOWrapper> nameServers = WebUtil.convertToVos(list);
        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        domain.updateNameServers(nameServers);
        List<NameServerVOWrapper> oldList = getOriginalNameServerList(getDomainId());

        if (WebUtil.isModefied(oldList, WebUtil.convertToVos(list))) {
            getVisitState().markDomainDirty(getDomainId(), DomainChangeType.ns);
            getVisitState().storeDomain(domain);
        } else {
            getVisitState().clearChange(getDomainId(), DomainChangeType.ns);
        }

        setNameServerListValue(null);
        getCallback().performCallback(getRequestCycle());
    }

    public List<NameServerVOWrapper> getOriginalNameServerList(long domainId) {
        try {
            return getAdminServices().getDomain(domainId).getNameServers();
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }

        return new ArrayList<NameServerVOWrapper>();
    }


    public void revert() {
        setNameServerListValue(null);
        getCallback().performCallback(getRequestCycle());
    }

    public boolean getIsRequestPending() {
        return getVisitState().getCurrentDomain(getDomainId()).isOperationPending();
    }

    public RequestsPerspective viewPendingRequests() {
        RequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new OpenTransactionForDomainsFetcher(Arrays.asList(getVisitState().getCurrentDomain(
            getDomainId()).getName()), getRzmServices()));
        return page;
    }

    public void setErrorField(IFormComponent field, String message) {
        super.setErrorField(field.getId(), message);
    }


}
