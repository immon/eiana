package org.iana.rzm.web.pages.user;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.common.AttributesEditor;
import org.iana.rzm.web.common.ContactAttributesEditor;
import org.iana.rzm.web.common.user.TransactionForDomainFetcher;
import org.iana.rzm.web.model.ContactVOWrapper;
import org.iana.rzm.web.model.DomainVOWrapper;

import java.util.HashMap;
import java.util.Map;

public abstract class UserContactEditor extends UserPage implements PageBeginRenderListener, ContactAttributesEditor,
        IExternalPage {

    public static final String PAGE_NAME = "user/UserContactEditor";

    @Component(
            id = "currentDetails", type = "Contact",
            bindings = {
                    "type=prop:contactType",
                    "domainId=prop:domainId",
                    "originalAttributes=prop:originalContact.map",
                    "contactAttributes=prop:originalContact.map",
                    "listener=literal:revert",
                    "editible=literal:false"
                    }
    )
    public abstract IComponent getCurrentDetailsComponent();

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:requestsPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "ShowPendingRequestsMessage",
            bindings = {"listener=listener:viewPendingRequests"}
    )
    public abstract IComponent getPendingRequestsMessageComponent();

    @Component(id = "editor", type = "ContactEditor",
            bindings = {"editor=prop:contactEditor", "contactAttributes=prop:contactAttributes"}
    )
    public abstract IComponent getContactAttributeEditorComponent();

    @InjectPage("user/ReviewDomain")
    public abstract ReviewDomain getReviewDomain();

    @InjectPage("user/UserRequestsPerspective")
    public abstract UserRequestsPerspective getRequestsPerspective();

    @Persist("client:page")
    public abstract void setContactType(String contactType);

    public abstract String getContactType();

    @Persist("client:page")
    public abstract void setDomainId(long id);

    public abstract long getDomainId();

    public abstract void setOriginalContact(ContactVOWrapper contact);
    public abstract ContactVOWrapper getOriginalContact();

    @Persist("client:page")
    public abstract void setContactAttributes(Map<String, String> attributes);
    public abstract Map<String, String> getContactAttributes();

    @Persist("client:page")
    public abstract DomainVOWrapper getModifiedDomain();
    public abstract void setModifiedDomain(DomainVOWrapper domain);

    public void pageBeginRender(PageEvent event) {

        setModifiedDomain(getVisitState().getMmodifiedDomain());

        if (getContactAttributes() == null) {
            setContactAttributes(new HashMap<String, String>());
        }

        try {
            DomainVOWrapper domain = getUserServices().getDomain(getDomainId());
            String sid = getContactAttributes().get(ContactVOWrapper.ID);
            ContactVOWrapper contactVOWrapper = domain.getContact(Long.parseLong(sid), getContactType());
            setOriginalContact(contactVOWrapper);
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        }
    }

    @SuppressWarnings("unchecked")
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {

        if(parameters.length == 0 || parameters.length < 3){
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        setContactAttributes((Map<String, String>) parameters[0]);
        setContactType(parameters[1].toString());
        setDomainId((Long)parameters[2]);
        try {
            restoreCurrentDomain(getDomainId());
            if(parameters.length == 4){
                restoreModifiedDomain((DomainVOWrapper) parameters[3]);
            }
        } catch (NoObjectFoundException e) {
            getExternalPageErrorHandler().handleExternalPageError("System Error restoring session");
        }
    }


    public AttributesEditor getContactEditor() {
        return this;
    }

    public void save(Map<String, String> attributes) {
        String type = getContactType();

        if (attributes.equals(getOriginalContact().getMap())) {
            goToReviewDomainPage();
            return;
        }

        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        domain.updateContactAttributes(attributes, type);
        getVisitState().markDomainDirty(getDomainId());
        goToReviewDomainPage();
    }

    public void revert() {
        goToReviewDomainPage();
    }


    public UserRequestsPerspective viewPendingRequests() {
        UserRequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new TransactionForDomainFetcher(getVisitState().getCurrentDomain(getDomainId()).getName(),
                getUserServices()));
        return page;
    }


    public boolean isRequestsPending() {
        return getVisitState().getCurrentDomain(getDomainId()).isOperationPending();
    }

    
    protected Object[] getExternalParameters() {
        DomainVOWrapper domain = getModifiedDomain();
        if(domain != null){
            return new Object[]{getContactAttributes(), getContactType(), getDomainId(), domain};
        }
        return new Object[]{getContactAttributes(), getContactType(), getDomainId()};
    }


    private void goToReviewDomainPage() {
        ReviewDomain reviewDomain = getReviewDomain();
        reviewDomain.setDomainId(getDomainId());
        getRequestCycle().activate(reviewDomain);
    }

}
