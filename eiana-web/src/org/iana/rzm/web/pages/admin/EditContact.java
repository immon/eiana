package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;

import java.util.*;

public abstract class EditContact extends AdminPage implements PageBeginRenderListener, ContactAttributesEditor {

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
            if(getOriginalContact() == null){
                DomainVOWrapper domain = getRzmServices().getDomain(getDomainId());
                String sid = getContactAttributes().get(ContactVOWrapper.ID);
                ContactVOWrapper contactVOWrapper = domain.getContact(Long.parseLong(sid), getContactType());
                setOriginalContact(contactVOWrapper);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }
    }

    //@SuppressWarnings("unchecked")
    //public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
    //
    //    if (parameters.length == 0 || parameters.length < 3) {
    //        getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
    //    }
    //
    //    setContactAttributes((Map<String, String>) parameters[0]);
    //    setContactType(parameters[1].toString());
    //    setDomainId((Long) parameters[2]);
    //    try {
    //        restoreCurrentDomain(getDomainId());
    //        if (parameters.length == 4) {
    //            restoreModifiedDomain((DomainVOWrapper) parameters[3]);
    //        }
    //    } catch (NoObjectFoundException e) {
    //        getExternalPageErrorHandler().handleExternalPageError("System Error restoring session");
    //    }
    //}


    public AttributesEditor getContactEditor() {
        return this;
    }

    public void save(Map<String, String> attributes) {
        String type = getContactType();

        if (attributes.equals(getOriginalContact().getMap())) {
            goToEditDomainPage();
            return;
        }

        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        domain.updateContactAttributes(attributes, type);
        getVisitState().markDomainDirty(getDomainId());
        goToEditDomainPage();
    }

    public void revert() {
        goToEditDomainPage();
    }


    public RequestsPerspective viewPendingRequests() {
        RequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new TransactionForDomainFetcher(getModifiedDomain().getName(), getRzmServices()));
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


    private void goToEditDomainPage() {
        EditDomain domain = getEditDomain();
        domain.setDomainId(getDomainId());
        getRequestCycle().activate(domain);
    }

}
