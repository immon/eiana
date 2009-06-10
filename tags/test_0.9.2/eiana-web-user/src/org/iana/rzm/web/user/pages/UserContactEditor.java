package org.iana.rzm.web.user.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.rzm.web.editors.*;

import java.util.*;

public abstract class UserContactEditor extends UserPage implements PageBeginRenderListener, ContactAttributesEditor,
        IExternalPage {

    public static final String PAGE_NAME = "UserContactEditor";

    @Component(
            id = "currentDetails", type = "rzmLib:Contact",
            bindings = {
                    "type=prop:contactType",
                    "domainId=prop:domainId",
                    "originalAttributes=prop:originalContact.map",
                    "contactAttributes=prop:originalContact.map",
                    "listener=literal:revert",
                    "editible=literal:false" ,
                    "contactServices=prop:contactServices",
                    "errorPage=prop:errorPage"
                    }
    )
    public abstract IComponent getCurrentDetailsComponent();

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:requestsPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "rzmLib:ShowPendingRequestsMessage",
            bindings = {"listener=listener:viewPendingRequests"}
    )
    public abstract IComponent getPendingRequestsMessageComponent();

    @Component(id = "editor", type = "rzmLib:ContactEditor",
            bindings = {"editor=prop:contactEditor", "contactAttributes=prop:contactAttributes"}
    )
    public abstract IComponent getContactAttributeEditorComponent();

    @Component(id = "soEditor", type = "rzmLib:SoContactEditor",
            bindings = {"editor=prop:contactEditor", "contactAttributes=prop:contactAttributes"}
    )
    public abstract IComponent getSoContactAttributeEditorComponent();

    @InjectPage(ReviewDomain.PAGE_NAME)
    public abstract ReviewDomain getReviewDomain();

    @InjectPage(UserRequestsPerspective.PAGE_NAME)
    public abstract UserRequestsPerspective getRequestsPerspective();

    @Persist("client")
    public abstract void setContactType(String contactType);
    public abstract String getContactType();

    @Persist("client")
    public abstract void setDomainId(long id);
    public abstract long getDomainId();

    public abstract void setOriginalContact(ContactVOWrapper contact);
    public abstract ContactVOWrapper getOriginalContact();

    @Persist("client")
    public abstract void setContactAttributes(Map<String, String> attributes);
    public abstract Map<String, String> getContactAttributes();

    @Persist("client")
    public abstract DomainVOWrapper getModifiedDomain();
    public abstract void setModifiedDomain(DomainVOWrapper domain);

    public boolean isSo(){
       return getContactType() != null && getContactType().equals(SystemRoleVOWrapper.SUPPORTING_ORGANIZATION);
    }

    public void pageBeginRender(PageEvent event) {

        setModifiedDomain(getVisitState().getModifiedDomain(getDomainId()));

        if (getContactAttributes() == null) {
            setContactAttributes(new HashMap<String, String>());
        }

        try {
            DomainVOWrapper domain = getUserServices().getDomain(getDomainId());
            String sid = getContactAttributes().get(ContactVOWrapper.ID);
            ContactVOWrapper contactVOWrapper = domain.getContact(Long.parseLong(sid), getContactType());
            setOriginalContact(contactVOWrapper);
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
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

        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        domain.updateContactAttributes(attributes, type);

        DomainChangeType changeType = DomainChangeType.fromString(type);

        if (attributes.equals(getOriginalContact().getMap())) {
            getVisitState().clearChange(getDomainId(),changeType);
        }else{
            getVisitState().markDomainDirty(getDomainId(),changeType);
            getVisitState().storeDomain(domain);
        }

        goToReviewDomainPage();
    }

    public void revert() {
        goToReviewDomainPage();
    }


    public UserRequestsPerspective viewPendingRequests() {
        UserRequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new OpenTransactionForDomainsRetriver(Arrays.asList(getVisitState().getCurrentDomain(getDomainId()).getName()), getUserServices()));
        page.setCallback(createCallback());
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
