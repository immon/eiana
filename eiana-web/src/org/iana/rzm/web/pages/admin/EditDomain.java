package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.engine.state.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.admin.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.admin.*;
import org.iana.rzm.web.tapestry.*;

import java.util.*;

public abstract class EditDomain extends AdminPage
    implements DomainAttributeEditor, PageBeginRenderListener, LinkTraget, IExternalPage {
    public static final String PAGE_NAME = "admin/EditDomain";

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:requestPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "ShowPendingRequestsMessage",
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

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domain.name"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "country", type = "Insert", bindings = {"value=prop:countryName"})
    public abstract IComponent getCountryComponent();

    @Component(id = "domainHeader", type = "DomainHeader", bindings = {"countryName=prop:countryName", "domainName=prop:domain.name"})
    public abstract IComponent getDomainHeaderComponentComponent();

    @InjectObject("infrastructure:applicationStateManager")
    public abstract ApplicationStateManager getApplicationStateManager();

    @Persist("client:page")
    public abstract long getDomainId();

    public abstract void setDomainId(long id);

    @Persist("client:page")
    public abstract String getCountryName();

    @Persist("client:page")
    public abstract SystemDomainVOWrapper getOriginalDomain();
    public abstract void setOriginalDomain(SystemDomainVOWrapper domain);


    public abstract void setCountryName(String name);

    @InjectPage("admin/RequestsPerspective")
    public abstract RequestsPerspective getRequestsPerspective();

    @InjectPage("admin/Domains")
    public abstract Domains getDomainsPage();

    @InjectPage("admin/EditContact")
    public abstract EditContact getEditContactPage();

    @InjectPage("admin/EditNameServerList")
    public abstract EditNameServerList getEditNameServerList();

    @InjectPage("admin/EditSubDomain")
    public abstract EditSubDomain getEditSubDomain();

    @InjectPage("admin/DomainChangesConfirmation")
    public abstract DomainChangesConfirmation getDomainChangesConfirmation();

    @Persist("client:page")
    public abstract void setModifiedDomain(DomainVOWrapper modifiedDomain);
    public abstract DomainVOWrapper getModifiedDomain();

    public void setIdentifier(Object id) {
        setModifiedDomain(getVisitState().getMmodifiedDomain());
        SystemDomainVOWrapper domain = null;
        try {
            domain = getAdminServices().getDomain(id.toString());
            setOriginalDomain(domain);
            setDomainId(domain.getId());
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }
    }


    protected Object[] getExternalParameters() {
        DomainVOWrapper modified = getModifiedDomain();
        if (modified != null) {
            return new Object[]{getDomainId(), modified};
        } else {
            return new Object[]{getDomainId()};
        }
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {

        if (parameters == null || parameters.length == 0) {
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
        }

        Long domainId = (Long) parameters[0];
        setDomainId(domainId);

        try {
            if (parameters.length == 2) {
                restoreModifiedDomain((DomainVOWrapper) parameters[1]);
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

            DomainVOWrapper mdomain = getVisitState().getMmodifiedDomain();
            setModifiedDomain(mdomain);
            getVisitState().markAsVisited(mdomain != null ? mdomain : domain);

            setModifiedDomain(getVisitState().getMmodifiedDomain());
                                                                      
            String countryName = getAdminServices().getCountryName(domain.getName());
            setCountryName(countryName);
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }
    }

    public DomainVOWrapper getDomain() {
        DomainVOWrapper domain = getVisitState().getMmodifiedDomain();
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
        DomainVOWrapper domain = getVisitState().getMmodifiedDomain();
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
        page.setEntityFetcher(new OpenTransactionForDomainsFetcher(Arrays.asList(getVisitState().getCurrentDomain(
            getDomainId()).getName()), getRzmServices()));
        return page;
    }


    public boolean isRequestPending() {
        return getVisitState().getCurrentDomain(getDomainId()).isOperationPending();
    }

    public void resetStateIfneeded() {
        getVisitState().resetModifirdDomain();
    }

    private static class DomainEntityEditorListener implements PageEditorListener<DomainVOWrapper> {

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

        public void saveEntity(DomainVOWrapper domainVOWrapper, IRequestCycle cycle) {
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
