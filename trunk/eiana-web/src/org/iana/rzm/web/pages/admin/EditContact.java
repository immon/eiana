package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;

import java.util.*;

public abstract class EditContact extends AdminPage
    implements PageBeginRenderListener, ContactAttributesEditor, IExternalPage {

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:requestsPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "ShowPendingRequestsMessage",
               bindings = {"listener=listener:viewPendingRequests", "pendigRequestMessage=literal:There is Request Pending for this domain."}
    )
    public abstract IComponent getPendingRequestsMessageComponent();

    @Component(id = "editor", type = "ContactEditor",
               bindings = {"editor=prop:contactEditor", "contactAttributes=prop:contactAttributes", "validationRequired=literal:false"}
    )
    public abstract IComponent getContactAttributeEditorComponent();

    @InjectPage("admin/EditDomain")
    public abstract EditDomain getEditDomain();

    @InjectPage("admin/RequestsPerspective")
    public abstract RequestsPerspective getRequestsPerspective();

    @Persist("client:page")
    public abstract void setContactType(String contactType);

    public abstract String getContactType();

    @Persist("client:page")
    public abstract void setDomainId(long id);

    public abstract long getDomainId();

    @Persist()
    public abstract ICallback getCallback();

    public abstract void setCallback(ICallback callback);

    public abstract void setOriginalContact(ContactVOWrapper contact);
    public abstract ContactVOWrapper getOriginalContact();

    @Persist()
    public abstract void setContactAttributes(Map<String, String> attributes);
    public abstract Map<String, String> getContactAttributes();

    @Persist()
    public abstract DomainVOWrapper getModifiedDomain();
    public abstract void setModifiedDomain(DomainVOWrapper domain);


    protected Object[] getExternalParameters() {
        return new Object[]{
            getDomainId(), getContactType(), getContactAttributes(), getCallback(), getModifiedDomain()
        };
    }

    @SuppressWarnings("unchecked")
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length < 4) {
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
        }
        Long domainId = (Long) parameters[0];
        setDomainId(domainId);
        setContactType((String) parameters[1]);
        setContactAttributes((Map<String, String>) parameters[2]);
        setCallback((ICallback) parameters[3]);
        try {
            restoreCurrentDomain(getDomainId());
            if (parameters.length > 4 && parameters[4] != null) {
                restoreModifiedDomain((DomainVOWrapper) parameters[4]);
            }
        } catch (NoObjectFoundException e) {
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
        }
    }


    public void pageBeginRender(PageEvent event) {
        setModifiedDomain(getVisitState().getModifiedDomain(getDomainId()));
        if (getContactAttributes() == null) {
            setContactAttributes(new HashMap<String, String>());
        }

        try {
            if (getOriginalContact() == null) {
                DomainVOWrapper domain = getRzmServices().getDomain(getDomainId());
                String sid = getContactAttributes().get(ContactVOWrapper.ID);
                ContactVOWrapper contactVOWrapper = domain.getContact(Long.parseLong(sid), getContactType());
                setOriginalContact(contactVOWrapper);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }
    }

    public AttributesEditor getContactEditor() {
        return this;
    }

    public void save(Map<String, String> attributes) {
        String type = getContactType();
        DomainChangeType changeType = DomainChangeType.fromString(type);
        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        domain.updateContactAttributes(attributes, type);

        if (attributes.equals(getOriginalContact().getMap())) {
            getVisitState().clearChange(getDomainId(), changeType);
        }else{
            getVisitState().markDomainDirty(getDomainId(), changeType);
            getVisitState().storeDomain(domain);
        }
        getCallback().performCallback(getRequestCycle());
    }

    public void revert() {
        getCallback().performCallback(getRequestCycle());
    }


    public RequestsPerspective viewPendingRequests() {
        RequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new OpenTransactionForDomainsFetcher(Arrays.asList(getVisitState().getCurrentDomain(
            getDomainId()).getName()), getRzmServices()));
        return page;
    }


    public boolean isRequestsPending() {
        return getVisitState().getCurrentDomain(getDomainId()).isOperationPending();
    }


    //protected Object[] getExternalParameters() {
    //    DomainVOWrapper domain = getModifiedDomain();
    //    if (domain != null) {
    //        return new Object[]{getContactAttributes(), getContactType(), getDomainId(), domain};
    //    }
    //    return new Object[]{getContactAttributes(), getContactType(), getDomainId()};
    //}


}
