package org.iana.rzm.web.pages.user;

import org.apache.log4j.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.tapestry.*;
import org.iana.rzm.web.util.*;

import java.util.*;


public abstract class ReviewDomain extends UserPage implements PageBeginRenderListener, IExternalPage, LinkTraget {

    public static final String PAGE_NAME = "user/ReviewDomain";
    private static final Logger LOGGER_SERVICE = Logger.getLogger(ReviewDomain.class.getName());


    @Component(id = "so", type = "Contact", bindings = {
            "type=literal:Sponsoring Organization",
            "contactAttributes=prop:domain.supportingOrganization.map",
            "domainId=prop:domain.id",
            "listener=listener:editContact",
            "editible=prop:editible",
            "rzmServices=prop:rzmServices",
            "errorPage=prop:errorPage"
            })
    public abstract IComponent getSOContactComponent();

    @Component(id = "listAdminContacts", type = "ListContacts", bindings = {
            "type=literal:Administrative",
            "contacts=prop:domain.adminContacts",
            "domainId=prop:domain.id",
            "action=listener:editContact",
            "editible=prop:editible",
            "rzmServices=prop:rzmServices",
            "errorPage=prop:errorPage"
            })
    public abstract IComponent getAdminContactsComponent();

    @Component(id = "listTechContacts", type = "ListContacts", bindings = {
            "type=literal:Technical",
            "contacts=prop:domain.technicalContacts",
            "domainId=prop:domain.id",
            "action=listener:editContact",
            "editible=prop:editible",
            "rzmServices=prop:rzmServices",
            "errorPage=prop:errorPage"
            })
    public abstract IComponent getTechContactsComponent();

    @Component(id = "listNameServers", type = "ListNameServers", bindings = {
            "nameServers=prop:nameServers",
            "domainId=prop:domain.id",
            "listener=listener:editNameServerList",
            "editible=prop:editible"
            })
    public abstract IComponent getListNameServerComponent();

    @Component(id = "subDomain", type = "SubDomain", bindings = {
            "registryUrl=prop:domain.registryUrl",
            "originalRegistryUrl=prop:originalDomain.registryUrl",
            "whoisServer=prop:domain.whoisServer",
            "originalWhoisServer=prop:originalDomain.whoisServer",
            "listener=listener:editSubDomain",
            "editible=prop:editible"
            })
    public abstract IComponent getSubDomainComponent();

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


    @Component(id = "overview", type = "PageLink", bindings = {"page=literal:user/UserHome",
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getHomeLinkComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domain.name"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:isRequestsPending"})
    public abstract IComponent getIsRequestComponent();

    @Component(id = "pendingRequestsMessage", type = "ShowPendingRequestsMessage", bindings = {
            "listener=listener:viewPendingRequests"
            })
    public abstract IComponent getShowPendingMessageComponent();


    @Component(id = "country", type = "Insert", bindings = {"value=prop:country"})
    public abstract IComponent getCountryNameComponent();


    @InjectPage("user/UserContactEditor")
    public abstract UserContactEditor getEditContactPage();

    @InjectPage("user/UserHome")
    public abstract UserHome getHomePage();

    @InjectPage("user/ReviewDomainChanges")
    public abstract ReviewDomainChanges getReviewChangesPage();

    @InjectPage("user/UserRequestsPerspective")
    public abstract UserRequestsPerspective getRequestsPerspective();

    @InjectPage("user/UserNameServerListEditor")
    public abstract UserNameServerListEditor getEditNameServerList();

    @InjectPage("user/UserSubDomainEditor")
    public abstract UserSubDomainEditor getUserSubDomainEditor();

    @Persist("client:page")
    public abstract void setDomainId(long domainId);
    public abstract long getDomainId();

    @Persist("client:page")
    public abstract DomainVOWrapper getModifiedDomain();
    public abstract void setModifiedDomain(DomainVOWrapper domain);

    public abstract DomainVOWrapper getOriginalDomain();
    public abstract void setOriginalDomain(DomainVOWrapper domain);


    protected Object[] getExternalParameters() {
        DomainVOWrapper modified = getModifiedDomain();
        if(modified != null){
            return new Object[]{getDomainId(), modified};
        }else{
            return new Object[]{getDomainId()};
        }
    }

    public void setIdentifier(Object id){
        try {
            SystemDomainVOWrapper domain = getUserServices().getDomain(id.toString());
            setDomainId(domain.getId());
            setOriginalDomain(domain);
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        }
    }

    public void pageBeginRender(PageEvent event) {

        try {
             DomainVOWrapper domain = getOriginalDomain();
            if(domain == null){
                domain = getUserServices().getDomain(getDomainId());
            }
            getVisitState().markAsVisited(domain);
            setOriginalDomain(domain);
            setModifiedDomain(getVisitState().getMmodifiedDomain());
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        }catch(AccessDeniedException e){
            getAccessDeniedHandler().handleAccessDenied(e, UserGeneralError.PAGE_NAME);
        }
    }

    public void activateExternalPage(Object[] params, IRequestCycle cycle) {
        if (params.length == 0 || params.length < 1) {
            getExternalPageErrorHandler().handleExternalPageError(
                    getMessageUtil().getSessionRestorefailedMessage());
        }

        Long domainId = (Long) params[0];
        setDomainId(domainId);
        try {
            if(params.length == 2){
                restoreModifiedDomain((DomainVOWrapper) params[1]);
            }
        } catch (NoObjectFoundException e) {
            getExternalPageErrorHandler().handleExternalPageError(
                    getMessageUtil().getSessionRestorefailedMessage());
        }
    }

    public boolean isModified() {
        return getVisitState().isDomainModified(getDomainId());
    }

    public boolean isEditible(){
        return !getIsRequestsPending();
    }

    public UserHome cancelEdit(long domainId) {
        getVisitState().markAsNotVisited(domainId);
        return getHomePage();
    }

    public ReviewDomainChanges saveEdit(long domainId) {
        ReviewDomainChanges page = getReviewChangesPage();
        page.setDomainId(getDomainId());
        page.setCountryName(getCountry());
        return page;
    }


    public UserContactEditor editContact(long contactId, String type) {
        UserContactEditor contactPage = getEditContactPage();
        contactPage.setDomainId(getDomain().getId());
        contactPage.setContactType(type);
        //todo Aske about multiple edits which contact to show
        ContactVOWrapper contact = getDomain().getContact(contactId, type);
        contactPage.setContactAttributes(contact.getMap());
        return contactPage;
    }

    public UserNameServerListEditor editNameServerList() {
        UserNameServerListEditor editNameServerList = getEditNameServerList();
        editNameServerList.setDomainId(getDomainId());
        return editNameServerList;
    }

    public UserSubDomainEditor editSubDomain(){
        UserSubDomainEditor editor = getUserSubDomainEditor();
        editor.setDomainId(getDomainId());
        editor.setOriginalDomain(getOriginalDomain());
        editor.setWhoisServer(getVisitState().getCurrentDomain(getDomainId()).getWhoisServer());
        editor.setRegistryUrl(getVisitState().getCurrentDomain(getDomainId()).getRegistryUrl());
        
        return editor;
    }


    public List<NameServerValue> getNameServers() {
        try {
            DomainVOWrapper domain = getUserServices().getDomain(getDomainId());
            List<NameServerVOWrapper> originals = domain.getNameServers();
            List<NameServerVOWrapper> current = new ArrayList<NameServerVOWrapper>(getDomain().getNameServers());
            return WebUtil.buildNameServerList(originals, current);
        }catch (NoObjectFoundException e) {
            LOGGER_SERVICE.warn("NoObjectFoundException " + getDomainId());
            return new ArrayList<NameServerValue>();
        }catch(AccessDeniedException e){
            LOGGER_SERVICE.warn("AccessDeniedException " + getDomainId());
            return new ArrayList<NameServerValue>();
        }
    }

    public boolean getIsRequestsPending() {
        return getDomain().isOperationPending();
    }

    public UserRequestsPerspective viewPendingRequests() {
        UserRequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new TransactionForDomainFetcher(getDomain().getName(), getUserServices()));
        return page;
    }

    public String getCountry() {
        return "(" + getUserServices().getCountryName(getDomain().getName()) + ")";
    }

    public DomainVOWrapper getDomain() {
        return getVisitState().getCurrentDomain(getDomainId());
    }

}
