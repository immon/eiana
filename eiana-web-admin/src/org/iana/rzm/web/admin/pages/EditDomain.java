package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.engine.state.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.admin.pages.editors.*;
import org.iana.rzm.web.admin.pages.listeners.*;
import org.iana.rzm.web.admin.services.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.callback.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.query.retriver.*;

import java.util.*;

public abstract class EditDomain extends AdminPage implements DomainAttributeEditor, PageBeginRenderListener, LinkTraget, IExternalPage {

    public static final String PAGE_NAME = "EditDomain";

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:requestPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "rzmLib:ShowPendingRequestsMessage",
               bindings = {"listener=listener:viewPendingRequests", "pendigRequestMessage=literal:There is a request pending for this domain."}
    )
    public abstract IComponent getPendingRequestsMessageComponent();

    @Component(id = "domainEditor",
               type = "DomainEditor",
               bindings = {
                   "domain=prop:domain",
                   "editor=prop:domainEditor",
                   "contactListener=listener:editContact",
                   "nameServerListener=listener:editNameServerList",
                   "subDomainListener=listener:editSubDomain"
                   })
    public abstract IComponent getDomainEditorComoponent();

    @Component(id = "domainHeader", type = "rzmLib:DomainHeader", bindings = {"countryName=prop:countryName", "domainName=prop:domain.name"})
    public abstract IComponent getDomainHeaderComponentComponent();

    @InjectObject("infrastructure:applicationStateManager")
    public abstract ApplicationStateManager getApplicationStateManager();

    @InjectPage(RequestsPerspective.PAGE_NAME)
    public abstract RequestsPerspective getRequestsPerspective();

    @InjectPage(Domains.PAGE_NAME)
    public abstract Domains getDomainsPage();

    @InjectPage(EditContact.PAGE_NAME)
    public abstract EditContact getEditContactPage();

    @InjectPage(EditNameServerList.PAGE_NAME)
    public abstract EditNameServerList getEditNameServerList();

    @InjectPage(EditSubDomain.PAGE_NAME)
    public abstract EditSubDomain getEditSubDomain();

    @InjectPage(DomainChangesConfirmation.PAGE_NAME)
    public abstract DomainChangesConfirmation getDomainChangesConfirmation();

    @Persist("client")
    public abstract long getDomainId();
    public abstract void setDomainId(long id);

    @Persist("client")
    public abstract String getCountryName();

    @Persist("client")
    public abstract SystemDomainVOWrapper getOriginalDomain();
    public abstract void setOriginalDomain(SystemDomainVOWrapper domain);

    @Persist("client")
    public abstract void setModifiedDomain(DomainVOWrapper modifiedDomain);
    public abstract DomainVOWrapper getModifiedDomain();

    @Persist("client")
    public abstract ICallback getCallback();
    public abstract void setCallback(ICallback callback);

    public abstract void setCountryName(String name);

    public void setIdentifier(Object id) {
        setModifiedDomain(getVisitState().getModifiedDomain(getDomainId()));
        SystemDomainVOWrapper domain = null;
        try {
            domain = getAdminServices().getDomain(id.toString());
            setOriginalDomain(domain);
            setDomainId(domain.getId());
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        }
    }

    protected Object[] getExternalParameters() {
        DomainVOWrapper modified = getModifiedDomain();
        if (modified != null) {
            return new Object[]{getDomainId(), getCallback(), modified};
        } else {
            return new Object[]{getDomainId(), getCallback()};
        }
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {

        if (parameters == null || parameters.length == 0) {
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
        }

        Long domainId = (Long) parameters[0];
        setDomainId(domainId);
        setCallback((ICallback) parameters[1]);

        try {
            if (parameters.length == 3) {
                if(getVisitState().getModifiedDomain(domainId) == null){
                    restoreModifiedDomain((DomainVOWrapper) parameters[2]);
                }
            }
        } catch (NoObjectFoundException e) {
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
        }
    }

    public void pageBeginRender(PageEvent event) {
        try {
            SystemDomainVOWrapper domain = getOriginalDomain();
            if (domain == null) {
                domain = getAdminServices().getDomain(getDomainId());
            }
            setOriginalDomain(domain);

            DomainVOWrapper mdomain = getVisitState().getModifiedDomain(getDomainId());
            setModifiedDomain(mdomain);
            getVisitState().markAsVisited(mdomain != null ? mdomain : domain);
            String countryName = getAdminServices().getCountryName(domain.getName());
            setCountryName(countryName);
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        }
    }

    public DomainVOWrapper getDomain() {
        DomainVOWrapper domain = getVisitState().getModifiedDomain(getDomainId());
        if(domain == null){
            domain = getVisitState().getCurrentDomain(getDomainId());
        }

        return domain;
    }

    public DomainAttributeEditor getDomainEditor() {
        return this;
    }

    public void save() {

        if (getVisitState().isDomainModified(getDomainId())) {
            DomainChangesConfirmation domainChangesConfirmation = getDomainChangesConfirmation();
            domainChangesConfirmation.setDomainId(getDomainId());
            domainChangesConfirmation.setCountryName(getCountryName());
            domainChangesConfirmation.setEditor(new DomainEntityEditorListener(getAdminServices(),
                                                                               new RzmCallback(getPageName(),isExternal(), getExternalParameters(),
                                                                                               getLogedInUserId()),
                                                                               getApplicationStateManager()));
            domainChangesConfirmation.setBorderHeader("DOMAINS");
            getRequestCycle().activate(domainChangesConfirmation);
        } else {
            revert();
        }
    }

    public void saveEntity() {
        DomainVOWrapper domain = getVisitState().getModifiedDomain(getDomainId());
        getVisitState().markAsNotVisited(getDomainId());
        getAdminServices().updateDomain(domain);
        goToDomains();
    }

    public void revert() {
        getVisitState().markAsNotVisited(getDomainId());
        goToDomains();
    }

    private void goToDomains() {
        Domains page = getDomainsPage();
        getRequestCycle().activate(page);
    }

    public void editContact(long contactId, String type) {
        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        ContactVOWrapper contact = domain.getContact(contactId, type);
        EditContact page = getEditContactPage();
        page.setCallback(new RzmCallback(PAGE_NAME, true, getExternalParameters(), getLogedInUserId()));
        page.setContactAttributes(contact.getMap());
        page.setDomainId(domain.getId());
        page.setContactType(type);
        getRequestCycle().activate(page);
    }

    public void editNameServerList() {
        EditNameServerList page = getEditNameServerList();
        page.setDomainId(getDomainId());
        page.setCallback(new RzmCallback(PAGE_NAME, true, getExternalParameters(), getLogedInUserId()));
        getRequestCycle().activate(page);
    }

    public void editSubDomain() {
        EditSubDomain editSubDomain = getEditSubDomain();
        editSubDomain.setDomainId(getDomainId());
        editSubDomain.setOriginalRegistryUrl(getOriginalDomain().getRegistryUrl());
        editSubDomain.setOriginalWhoisServer(getOriginalDomain().getWhoisServer());
        editSubDomain.setRegistryUrl(getDomain().getRegistryUrl());
        editSubDomain.setWhoisServer(getDomain().getWhoisServer());
        editSubDomain.setCallback(new RzmCallback(PAGE_NAME, true, getExternalParameters(), getLogedInUserId()));
        getRequestCycle().activate(editSubDomain);
    }

    public RequestsPerspective viewPendingRequests() {
        RequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new OpenTransactionForDomainsRetriver(Arrays.asList(getVisitState().getCurrentDomain(
            getDomainId()).getName()), getRzmServices()));
         page.setCallback(new RzmCallback(PAGE_NAME, true, getExternalParameters(), getLogedInUserId()));
        return page;
    }


    public boolean isRequestPending() {
        return getVisitState().getCurrentDomain(getDomainId()).isOperationPending();
    }

    public void resetStateIfneeded() {
        getVisitState().resetModifiedDomain(getDomainId());
    }

    private static class DomainEntityEditorListener implements PageEditorListener<DomainVOWrapper, DomainChangesConfirmation> {

        private AdminServices services;
        private ICallback callback;
        private ApplicationStateManager stateManager;
        
        public DomainEntityEditorListener(AdminServices services,
                                          ICallback callback,
                                          ApplicationStateManager stateManager) {
            this.services = services;
            this.callback = callback;
            this.stateManager = stateManager;
        }

        public void saveEntity(DomainChangesConfirmation adminPage, DomainVOWrapper domainVOWrapper, IRequestCycle cycle, boolean checkRadicalChanges) {
            services.updateDomain(domainVOWrapper);
            Visit visit = (Visit) stateManager.get("visit");
            visit.markAsNotVisited(domainVOWrapper.getId());
            cycle.activate(Domains.PAGE_NAME);
        }

        public void cancel(IRequestCycle cycle) {
            callback.performCallback(cycle);
        }
    }

}
