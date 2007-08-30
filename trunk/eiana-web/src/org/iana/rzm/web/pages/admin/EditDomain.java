package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.tapestry.*;

public abstract class EditDomain extends AdminPage implements DomainAttributeEditor, PageBeginRenderListener, LinkTraget {

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:requestPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "ShowPendingRequestsMessage",
               bindings = {"listener=listener:viewPendingRequests", "pendigRequestMessage=literal:There is Request Pending for this domain."}
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

    @Persist("client:page")
    public abstract long getDomainId();

    public abstract void setDomainId(long id);

    @Persist("client:page")
    public abstract String getCountryName();

    @Persist("client:page")
    public abstract void setOriginalDomain(SystemDomainVOWrapper domain);
    public abstract SystemDomainVOWrapper getOriginalDomain();

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

    public abstract void setModifiedDomain(DomainVOWrapper modifiedDomain);

    public void setIdentifier(Object id){
        SystemDomainVOWrapper domain = getAdminServices().getDomain(id.toString());
        setOriginalDomain(domain);
        setDomainId(domain.getId());
    }

    public void pageBeginRender(PageEvent event) {
        try {
            SystemDomainVOWrapper domain =  getOriginalDomain();
            if(domain == null){
                domain = getAdminServices().getDomain(getDomainId());
            }
            getVisitState().markAsVisited(domain);
            setOriginalDomain(domain);
            setModifiedDomain(getVisitState().getMmodifiedDomain());
            String countryName = getAdminServices().getCountryName(domain.getName());
            setCountryName(countryName);
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }
    }

    public DomainVOWrapper getDomain(){
        return getVisitState().getCurrentDomain(getDomainId());
    }

    public DomainAttributeEditor getDomainEditor() {
        return this;
    }

    public void save() {
        if(getVisitState().isDomainModified(getDomainId())){

        }else{
            revert();
        }
    }

    public void revert() {
        Domains page = getDomainsPage();
        getVisitState().markAsNotVisited(getDomainId());
        getRequestCycle().activate(page);
    }

    public void editContact(long contactId, String type) {
        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        ContactVOWrapper contact = domain.getContact(contactId, type);
        EditContact page = getEditContactPage();
        page.setContactAttributes(contact.getMap());
        page.setDomainId(domain.getId());
        page.setContactType(type);
        getRequestCycle().activate(page);
    }

    public void editNameServerList() {
        EditNameServerList nameServerListt = getEditNameServerList();
        nameServerListt.setDomainId(getDomainId());
        getRequestCycle().activate(nameServerListt);
    }

    public void editSubDomain(){
        EditSubDomain editSubDomain = getEditSubDomain();
        editSubDomain.setDomainId(getDomainId());
        editSubDomain.setOriginalRegistryUrl(getOriginalDomain().getRegistryUrl());
        editSubDomain.setOriginalWhoisServer(getOriginalDomain().getWhoisServer());
        editSubDomain.setRegistryUrl(getDomain().getRegistryUrl());
        editSubDomain.setWhoisServer(getDomain().getWhoisServer());
        getRequestCycle().activate(editSubDomain);
    }

     public RequestsPerspective viewPendingRequests() {
        RequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new TransactionForDomainFetcher(getDomain().getName(), getRzmServices()));
        return page;
    }


    public boolean isRequestPending() {
        return getVisitState().getCurrentDomain(getDomainId()).isOperationPending();
    }

}
