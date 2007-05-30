package org.iana.rzm.web.pages.user;

import org.apache.log4j.Logger;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.common.user.TransactionForDomainFetcher;
import org.iana.rzm.web.model.ContactVOWrapper;
import org.iana.rzm.web.model.DomainVOWrapper;
import org.iana.rzm.web.model.NameServerVOWrapper;
import org.iana.rzm.web.model.NameServerValue;
import org.iana.rzm.web.util.ListUtil;

import java.util.ArrayList;
import java.util.List;


public abstract class ReviewDomain extends UserPage implements PageBeginRenderListener, IExternalPage {

    public static final String PAGE_NAME = "user/ReviewDomain";
    private static final Logger LOGGER_SERVICE = Logger.getLogger(ReviewDomain.class.getName());


    @Component(id = "so", type = "Contact", bindings = {
            "type=literal:Sponsoring Organization",
            "contactAttributes=prop:domain.supportingOrganization.map",
            "domainId=prop:domain.id",
            "listener=listener:editContact"
            })
    public abstract IComponent getSOContactComponent();

    @Component(id = "listAdminContacts", type = "ListContacts", bindings = {
            "type=literal:Administrative",
            "contacts=prop:domain.adminContacts",
            "domainId=prop:domain.id",
            "action=listener:editContact"
            })
    public abstract IComponent getAdminContactsComponent();

    @Component(id = "listTechContacts", type = "ListContacts", bindings = {
            "type=literal:Technical",
            "contacts=prop:domain.technicalContacts",
            "domainId=prop:domain.id",
            "action=listener:editContact"
            })
    public abstract IComponent getTechContactsComponent();

    @Component(id = "listNameServers", type = "ListNameServers", bindings = {
            "nameServers=prop:nameServers",
            "domainId=prop:domain.id",
            "listener=listener:editNameServerList"
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


    @Component(id = "overview", type = "PageLink", bindings = {"page=literal:user/UserHome"})
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

    public void pageBeginRender(PageEvent event) {

        try {
            DomainVOWrapper domain = getUserServices().getDomain(getDomainId());
            getVisitState().markAsVisited(domain);
            setOriginalDomain(domain);
            setModifiedDomain(getVisitState().getMmodifiedDomain());
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e);
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


    public List<NameServerValue> getNameServers() {

        try {
            DomainVOWrapper domain = getUserServices().getDomain(getDomainId());
            List<NameServerVOWrapper> originals = domain.getNameServers();
            List<NameServerVOWrapper> current = new ArrayList<NameServerVOWrapper>(getDomain().getNameServers());
            List<NameServerValue> all = new ArrayList<NameServerValue>();

            for (NameServerVOWrapper wrapper : originals) {
                NameServerVOWrapper currentVO = findNameServer(wrapper.getId(), current);
                NameServerValue nameServerValue = new NameServerValue(currentVO == null ? wrapper : currentVO);
                if (currentVO == null) {
                    nameServerValue.setStatus(NameServerValue.DELETE);
                } else {
                    String status = currentVO.equals(wrapper) ? NameServerValue.DEFAULT : NameServerValue.MODIFIED;
                    nameServerValue.setStatus(status);
                    current.remove(currentVO);
                }
                all.add(nameServerValue);
            }
            current.removeAll(originals);
            for (NameServerVOWrapper wrapper : current) {
                all.add(new NameServerValue(wrapper).setStatus(NameServerValue.NEW));
            }
            return all;
        } catch (NoObjectFoundException e) {
            LOGGER_SERVICE.warn("NoObjectFoundException " + getDomainId());
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


    private NameServerVOWrapper findNameServer(final long id, List<NameServerVOWrapper> list) {
        return ListUtil.find(list, new ListUtil.Predicate<NameServerVOWrapper>() {
            public boolean evaluate(NameServerVOWrapper object) {
                return object.getId() == id;
            }
        });

    }

}
