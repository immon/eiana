package org.iana.rzm.web.user.pages;

import org.apache.log4j.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.rzm.web.common.utils.*;
import org.iana.rzm.web.user.query.retriver.*;

import java.util.*;


public abstract class ReviewDomain extends UserPage implements PageBeginRenderListener, IExternalPage, LinkTraget {

    public static final String PAGE_NAME = "ReviewDomain";
    private static final Logger LOGGER_SERVICE = Logger.getLogger(ReviewDomain.class.getName());

    @Component(id = "so", type = "rzmLib:Contact", bindings = {
            "type=literal:Sponsoring Organization",
            "domainId=prop:domain.id",
            "contactAttributes=prop:domain.supportingOrganization.map",
            "listener=listener:editContact",
            "editible=prop:dataEditable",
            "contactServices=prop:contactServices",
            "errorPage=prop:errorPage"
            })
    public abstract IComponent getSOContactComponent();

    @Component(id = "listAdminContacts", type = "ListContacts", bindings = {
            "type=literal:Administrative",
            "contacts=prop:domain.adminContacts",
            "domainId=prop:domain.id",
            "action=listener:editContact",
            "editible=prop:dataEditable",
             "contactServices=prop:contactServices",
            "errorPage=prop:errorPage"
            })
    public abstract IComponent getAdminContactsComponent();

    @Component(id = "listTechContacts", type = "ListContacts", bindings = {
            "type=literal:Technical",
            "contacts=prop:domain.technicalContacts",
            "domainId=prop:domain.id",
            "action=listener:editContact",
            "editible=prop:dataEditable",
             "contactServices=prop:contactServices",
            "errorPage=prop:errorPage"
            })
    public abstract IComponent getTechContactsComponent();

    @Component(id = "listNameServers", type = "rzmLib:ListNameServers", bindings = {
            "nameServers=prop:nameServers",
            "domainId=prop:domain.id",
            "listener=listener:editNameServerList",
            "editible=prop:nsChageEditible"
            })
    public abstract IComponent getListNameServerComponent();

    @Component(id = "subDomain", type = "rzmLib:SubDomain", bindings = {
            "registryUrl=prop:domain.registryUrl",
            "originalRegistryUrl=prop:originalDomain.registryUrl",
            "whoisServer=prop:domain.whoisServer",
            "originalWhoisServer=prop:originalDomain.whoisServer",
            "listener=listener:editSubDomain",
            "editible=prop:dataEditable"
            })
    public abstract IComponent getSubDomainComponent();

    @Component(id = "isModified", type = "If", bindings = {
            "condition=prop:modified"
            })
    public abstract IComponent getIsModifiedComponent();

    @Component(id = "cancelEdit", type = "DirectLink", bindings = {
            "listener=listener:cancelEdit",
            "parameters=prop:domainId",
            "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"
            })
    public abstract IComponent getCancelEditComponent();

    @Component(id = "completeEdit", type = "DirectLink", bindings = {
            "listener=listener:saveEdit",
            "parameters=prop:domainId",
            "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"
            })
    public abstract IComponent getSaveEditComponent();


    @Component(id = "overview", type = "PageLink", bindings = {"page=literal:Home",
            "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getHomeLinkComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domain.name"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:isRequestsPending"})
    public abstract IComponent getIsRequestComponent();

    @Component(id = "pendingRequestsMessage", type = "rzmLib:ShowPendingRequestsMessage", bindings = {
            "listener=listener:viewPendingRequests"
            })
    public abstract IComponent getShowPendingMessageComponent();

    @Component(id = "pendingGlueRequest", type = "If", bindings = {"condition=prop:glueMember"})
    public abstract IComponent getIsGluePendingComponent();

      @Component(id = "pendingGlueMessage", type = "rzmLib:ShowPendingRequestsMessage", bindings = {
            "listener=listener:viewGlueRequests",
            "pendigRequestMessage=literal:This domain is part of a Glue change. Edits to Name Servers are disabled until the currently glue change is resolved "
            })
    public abstract IComponent getPendingGlueMessage();



    @Component(id = "country", type = "Insert", bindings = {"value=prop:country"})
    public abstract IComponent getCountryNameComponent();

    @Component(id = "domainHeader", type = "rzmLib:DomainHeader", bindings = {"countryName=prop:country", "domainName=prop:domain.name"})
    public abstract IComponent getDomainHeaderComponentComponent();


    @InjectPage(UserContactEditor.PAGE_NAME)
    public abstract UserContactEditor getEditContactPage();

    @InjectPage(Home.PAGE_NAME)
    public abstract Home getHomePage();

    @InjectPage(ReviewDomainChanges.PAGE_NAME)
    public abstract ReviewDomainChanges getReviewChangesPage();

    @InjectPage(UserRequestsPerspective.PAGE_NAME)
    public abstract UserRequestsPerspective getRequestsPerspective();

    @InjectPage(UserNameServerListEditor.PAGE_NAME)
    public abstract UserNameServerListEditor getEditNameServerList();

    @InjectPage(UserSubDomainEditor.PAGE_NAME)
    public abstract UserSubDomainEditor getUserSubDomainEditor();

    @Persist("client")
    public abstract void setDomainId(long domainId);
    public abstract long getDomainId();

    @Persist("client")
    public abstract DomainVOWrapper getModifiedDomain();
    public abstract void setModifiedDomain(DomainVOWrapper domain);

    public abstract boolean isGlueMember();
    public abstract void setGlueMember(boolean value);

    public abstract DomainVOWrapper getOriginalDomain();
    public abstract void setOriginalDomain(DomainVOWrapper domain);

    public abstract void setCallback(ICallback callback);


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
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        }
    }

    public void pageBeginRender(PageEvent event) {

        try {
             DomainVOWrapper domain = getOriginalDomain();
            if(domain == null){
                domain = getUserServices().getDomain(getDomainId());
            }
            DomainVOWrapper modifiedDomain = getVisitState().getModifiedDomain(getDomainId());
            setModifiedDomain(modifiedDomain);
            getVisitState().markAsVisited(modifiedDomain == null ? domain : modifiedDomain);
            setOriginalDomain(domain);
            int count =
                getUserServices().getTransactionCount(QueryBuilderUtil.impactedParty(Arrays.asList(domain.getName())));
            setGlueMember(count > 0);
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        }catch(AccessDeniedException e){
            getAccessDeniedHandler().handleAccessDenied(e, GeneralError.PAGE_NAME);
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
            
    public boolean isDataEditable(){
        return !getIsRequestsPending();
    }

    public boolean isNsChageEditible(){
        return !getIsRequestsPending() && !isGlueMember();
    }


    public Home cancelEdit(long domainId) {
        getVisitState().markAsNotVisited(domainId);
        return getHomePage();
    }

    public ReviewDomainChanges saveEdit(long domainId) {
        try {
            ReviewDomainChanges page = getReviewChangesPage();
            page.setDomainId(getDomainId());
            page.setCountryName(getCountry());
            page.setTransactionChanges(getUserServices().getChanges(getDomain()));
            return page;
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        } catch (RadicalAlterationException e) {
            setErrorMessage(getMessageUtil().getRadicalAlterationCheckMessage(e.getDomainName()));
        } catch (SharedNameServersCollisionException e) {
            setErrorMessage(getMessageUtil().getSharedNameServersCollisionMessage(e.getNameServers()));
        }
        return null;
    }


    public UserContactEditor editContact(long contactId, String type) {
        UserContactEditor contactPage = getEditContactPage();
        contactPage.setDomainId(getDomain().getId());
        contactPage.setContactType(type);
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
        page.setEntityFetcher(new OpenTransactionForDomainsRetriver(Arrays.asList(getDomain().getName()), getUserServices()));
        page.setCallback(createCallback());
        return page;
    }

    public UserRequestsPerspective viewGlueRequests(){
        UserRequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new ImpactedPartyTransactionRetriver(Arrays.asList(getDomain().getName()), getUserServices()));
        page.setCallback(createCallback());
        page.setImpactedParty(true);
        return page;
    }

    public String getCountry() {
        DomainVOWrapper wrapper = getDomain();
        if(wrapper == null){
            return "";
        }

        return getUserServices().getCountryName(wrapper.getName());
    }

    public DomainVOWrapper getDomain() {
        return getVisitState().getCurrentDomain(getDomainId());
    }


}
