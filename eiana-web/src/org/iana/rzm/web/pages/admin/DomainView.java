package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.engine.state.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.admin.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.admin.*;
import org.iana.rzm.web.tapestry.*;
import org.iana.rzm.web.util.*;

import java.util.*;

public abstract class DomainView extends AdminPage implements PageBeginRenderListener, IExternalPage {

    public static final String PAGE_NAME = "admin/DomainView";

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domain.name"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "country", type = "Insert", bindings = {"value=prop:countryName"})
    public abstract IComponent getCountryNameComponent();

    @Component(id = "domainHeader",
               type = "DomainHeader",
               bindings = {"countryName=prop:countryName", "domainName=prop:domain.name"})
    public abstract IComponent getDomainHeaderComponentComponent();

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:requestPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "ShowPendingRequestsMessage",
               bindings = {"listener=listener:viewPendingRequests", "pendigRequestMessage=literal:There is Request Pending for this domain."}
    )
    public abstract IComponent getPendingRequestsMessageComponent();

    @Component(id = "overview", type = "DirectLink", bindings = {"listener=listener:back",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getHomeLinkComponent();


    @Component(id = "subDomain", type = "SubDomain", bindings = {
        "registryUrl=prop:domain.registryUrl",
        "originalRegistryUrl=prop:originalDomain.registryUrl",
        "whoisServer=prop:domain.whoisServer",
        "originalWhoisServer=prop:originalDomain.whoisServer",
        "listener=listener:editSubDomain",
        "editible=literal:true"
        })
    public abstract IComponent getSubDomainComponent();

    @Component(id = "so", type = "DomainContactEditor",
               bindings = {
                   "contactType=literal:Sponsoring Organization",
                   "domainId=prop:domainId",
                   "originalContact=prop:soOriginalContact",
                   "contact=prop:soContact",
                   "contactListener=listener:editContact"
                   }
    )
    public abstract IComponent getSoContactComponent();

    @Component(id = "ac", type = "DomainContactEditor",
               bindings = {
                   "contactType=literal:Administrative",
                   "domainId=prop:domainId",
                   "originalContact=prop:acOriginalContact",
                   "contact=prop:acContact",
                   "contactListener=listener:editContact"
                   }
    )
    public abstract IComponent getAcContactComponent();

    @Component(id = "tc", type = "DomainContactEditor",
               bindings = {
                   "contactType=literal:Technical",
                   "domainId=prop:domainId",
                   "originalContact=prop:tcOriginalContact",
                   "contact=prop:tcContact",
                   "contactListener=listener:editContact"
                   }
    )
    public abstract IComponent getTcContactComponent();

    @Component(id = "listNameServers", type = "ListNameServers", bindings = {
        "nameServers=prop:nameServers",
        "domainId=prop:domain.id",
        "listener=listener:editNameServerList",
        "editible=literal:true"
        })
    public abstract IComponent getListNameServerComponent();

    @Component(id = "isModified", type = "If", bindings = {
        "condition=prop:modified"
        })
    public abstract IComponent getIsModifiedComponent();

    @Component(id = "cancelEdit", type = "DirectLink", bindings = {
        "listener=listener:cancelEdit",
        "parameters=prop:domainId",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"
        })
    public abstract IComponent getCancelEditComponent();

    @Component(id = "completeEdit", type = "DirectLink", bindings = {
        "listener=listener:saveEdit",
        "parameters=prop:domainId",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"
        })
    public abstract IComponent getSaveEditComponent();

    @InjectObject("infrastructure:applicationStateManager")
    public abstract ApplicationStateManager getApplicationStateManager();

    @InjectPage("admin/RequestsPerspective")
    public abstract RequestsPerspective getRequestsPerspective();

    @InjectPage("admin/EditContact")
    public abstract EditContact getEditContactPage();

    @InjectPage("admin/EditNameServerList")
    public abstract EditNameServerList getEditNameServerList();

    @InjectPage("admin/EditSubDomain")
    public abstract EditSubDomain getEditSubDomain();

    @InjectPage("admin/DomainChangesConfirmation")
    public abstract DomainChangesConfirmation getDomainChangesConfirmation();

    @InjectPage(AdminHome.PAGE_NAME)
    public abstract IPage getStartPage();

    @Persist("client")
    public abstract String getCountryName();
    public abstract void setCountryName(String countryName);

    @Persist("client")
    public abstract long getDomainId();
    public abstract void setDomainId(long id);

    @Persist("client")
    public abstract void setOriginalDomain(DomainVOWrapper domain);
    public abstract DomainVOWrapper getOriginalDomain();

    @Persist("client")
    public abstract void setModifiedDomain(DomainVOWrapper domain);
    public abstract DomainVOWrapper getModifiedDomain();

    @Persist("client")
    public abstract ICallback getCallback();
    public abstract void setCallback(ICallback callback);

    @Persist("client")
    public abstract void setRequestMetaParameters(RequestMetaParameters metaParameters);
    public abstract RequestMetaParameters getRequestMetaParameters();

    public DomainVOWrapper getDomain() {
        return getVisitState().getCurrentDomain(getDomainId());
    }

    protected Object[] getExternalParameters() {
        if (getModifiedDomain() != null) {
            return new Object[]{getDomainId(), getCallback(), getRequestMetaParameters(), getModifiedDomain()};
        }
        return new Object[]{getDomainId(), getCallback(), getRequestMetaParameters()};
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length < 2) {
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
        }

        Long id = (Long) parameters[0];
        setDomainId(id);
        setCallback((ICallback) parameters[1]);
        setRequestMetaParameters((RequestMetaParameters) parameters[2]);


        try {
            if (parameters.length == 4) {
                restoreModifiedDomain((DomainVOWrapper) parameters[3]);
            }
        } catch (NoObjectFoundException e) {
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
        }

        getVisitState().setRequestMetaParameters(getRequestMetaParameters());
    }

    public void pageBeginRender(PageEvent event) {
        try {
            if (getOriginalDomain() == null) {
                DomainVOWrapper domain = getAdminServices().getDomain(getDomainId());
                setOriginalDomain(domain);
                DomainVOWrapper mdomain = getVisitState().getModifiedDomain(getDomainId());
                setModifiedDomain(mdomain);
                getVisitState().markAsVisited(mdomain != null ? mdomain : domain);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }

        String countryName = getAdminServices().getCountryName(getOriginalDomain().getName());
        setCountryName(countryName);
    }

    public ContactVOWrapper getAcOriginalContact() {
        return getOriginalDomain().getAdminContact();
    }

    public ContactVOWrapper getTcOriginalContact() {
        return getOriginalDomain().getTechnicalContact();
    }

    public ContactVOWrapper getSoOriginalContact() {
        return getOriginalDomain().getSupportingOrganization();
    }

    public ContactVOWrapper getSoContact() {
        return getVisitState().getCurrentDomain(getDomainId()).getSupportingOrganization();
    }

    public ContactVOWrapper getTcContact() {
        return getVisitState().getCurrentDomain(getDomainId()).getTechnicalContact();
    }

    public ContactVOWrapper getAcContact() {
        return getVisitState().getCurrentDomain(getDomainId()).getAdminContact();
    }

    public boolean isRequestPending() {
        return getVisitState().getCurrentDomain(getDomainId()).isOperationPending();
    }

    public List<NameServerValue> getNameServers() {
        List<NameServerVOWrapper> originals = getOriginalDomain().getNameServers();
        List<NameServerVOWrapper> current = new ArrayList<NameServerVOWrapper>(getDomain().getNameServers());
        return WebUtil.buildNameServerList(originals, current);
    }

    public void editContact(long contactId, String type) {
        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        ContactVOWrapper contact = domain.getContact(contactId, type);
        EditContact page = getEditContactPage();
        page.setContactAttributes(contact.getMap());
        page.setDomainId(domain.getId());
        page.setContactType(type);
        page.setCallback(new RzmCallback(PAGE_NAME, true, getListenerParmeters(),getLogedInUserId()));
        getRequestCycle().activate(page);
    }

    public void editNameServerList() {
        EditNameServerList page = getEditNameServerList();
        page.setDomainId(getDomainId());
        page.setCallback(new RzmCallback(PAGE_NAME, true, getListenerParmeters(),getLogedInUserId()));
        getRequestCycle().activate(page);
    }

    public void editSubDomain() {
        EditSubDomain editSubDomain = getEditSubDomain();
        editSubDomain.setDomainId(getDomainId());
        editSubDomain.setOriginalRegistryUrl(getOriginalDomain().getRegistryUrl());
        editSubDomain.setOriginalWhoisServer(getOriginalDomain().getWhoisServer());
        editSubDomain.setRegistryUrl(getVisitState().getCurrentDomain(getDomainId()).getRegistryUrl());
        editSubDomain.setWhoisServer(getVisitState().getCurrentDomain(getDomainId()).getWhoisServer());
        editSubDomain.setCallback(new RzmCallback(PAGE_NAME, true, getListenerParmeters(),getLogedInUserId()));
        getRequestCycle().activate(editSubDomain);
    }

    private Object[] getListenerParmeters() {
         return new Object[]{getDomainId(), getCallback(), getRequestMetaParameters()};
    }

    public void back() {
        getCallback().performCallback(getRequestCycle());
    }

    public RequestsPerspective viewPendingRequests() {
        RequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new OpenTransactionForDomainsFetcher(Arrays.asList(getDomain().getName()),
                                                                   getRzmServices()));
        return page;
    }

    public boolean getIsRequestsPending() {
        return getDomain().isOperationPending();
    }

    public boolean isModified() {
        return getVisitState().isDomainModified(getDomainId());
    }

    public void cancelEdit(long domainId) {
        getVisitState().markAsNotVisited(domainId);
        getRequestCycle().activate(getStartPage());
    }

    public void saveEdit() {

        try {
           TransactionActionsVOWrapper  changes = getAdminServices().getChanges(getDomain());
            RzmCallback callback = new RzmCallback(PAGE_NAME, true, getExternalParameters(), getLogedInUserId());
            TransactionDomainEntityEditorListener editor = new TransactionDomainEntityEditorListener(getAdminServices(), changes, callback, getApplicationStateManager());
            editor.setMessageUtil(getMessageUtil());
            DomainChangesConfirmation page = getDomainChangesConfirmation();
            page.setEditor(editor);
            page.setDomainId(getDomainId());
            page.setBorderHeader("REQUESTS");
            getRequestCycle().activate(page);
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        } catch (RadicalAlterationException e) {
            setErrorMessage(getMessageUtil().getAllNameServersChangeMessage());
        } catch (SharedNameServersCollisionException e) {
            setErrorMessage(getMessageUtil().getSharedNameServersCollisionMessage(e.getNameServers()));
        }

    }

    public void resetStateIfneeded() {
        getVisitState().markAsNotVisited(getDomainId());
    }


    private static class TransactionDomainEntityEditorListener implements PageEditorListener<DomainVOWrapper> {

        private AdminServices services;
        private TransactionActionsVOWrapper changes;
        private ICallback callback;
        private ApplicationStateManager manager;
        private MessageUtil messageUtil;

        public TransactionDomainEntityEditorListener(AdminServices services, TransactionActionsVOWrapper changes, ICallback callback, ApplicationStateManager manager) {
            this.services = services;
            this.changes = changes;
            this.callback = callback;
            this.manager = manager;
        }

        public void saveEntity(AdminPage adminPage, DomainVOWrapper domainVOWrapper, IRequestCycle cycle)
            throws
            NoObjectFoundException,
            NoDomainModificationException,
            TransactionExistsException,
            NameServerChangeNotAllowedException {


            if (changes.offerSeparateRequest() || changes.mustSplitrequest()) {
                RequestSplitConfirmation page =
                    (RequestSplitConfirmation) cycle.getPage(RequestSplitConfirmation.PAGE_NAME);
                page.setDomainId(domainVOWrapper.getId());
                page.setDomainName(domainVOWrapper.getName());
                page.setCallback(callback);
                page.setMustSplit(changes.mustSplitrequest());
                cycle.activate(page);
                return;
            }

            Summary page = (Summary) cycle.getPage(Summary.PAGE_NAME);
            page.setCallback(new PageCallback(AdminHome.PAGE_NAME));
            page.setDomainName(domainVOWrapper.getName());
            page.setDomainId(domainVOWrapper.getId());
            boolean split = changes.mustSplitrequest();
            List<TransactionVOWrapper> list = null;
            Visit visit = (Visit) manager.get("visit");
            try {
                list = services.createDomainModificationTrunsaction(domainVOWrapper, split, visit.getRequestMetaParameters());
                visit.markAsNotVisited(domainVOWrapper.getId());
                page.setTikets(list);
                cycle.activate(page);
            } catch (DNSTechnicalCheckExceptionWrapper e) {
                adminPage.setErrorMessage(e.getMessage());
            } catch (SharedNameServersCollisionException e) {
                adminPage.setErrorMessage(messageUtil.getSharedNameServersCollisionMessage(e.getNameServers()));
            } catch (RadicalAlterationException e) {
                adminPage.setErrorMessage(messageUtil.getAllNameServersChangeMessage());
            }
            cycle.activate(adminPage);
        }

        public void cancel(IRequestCycle cycle) {
            callback.performCallback(cycle);
        }

        public void setMessageUtil(MessageUtil messageUtil) {
            this.messageUtil = messageUtil;
        }
    }
}
