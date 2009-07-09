package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.common.DomainChangeType;
import org.iana.rzm.web.common.model.ContactVOWrapper;
import org.iana.rzm.web.common.model.DomainVOWrapper;
import org.iana.rzm.web.common.query.retriver.OpenTransactionForDomainsRetriver;
import org.iana.rzm.web.tapestry.editors.AttributesEditor;
import org.iana.rzm.web.tapestry.editors.ContactAttributesEditor;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public abstract class EditContact extends AdminPage implements PageBeginRenderListener, ContactAttributesEditor, IExternalPage {

    public static final String PAGE_NAME = "EditContact";

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:requestsPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "rzmLib:ShowPendingRequestsMessage",
               bindings = {"listener=listener:viewPendingRequests", "pendigRequestMessage=literal:There is Request Pending for this domain."}
    )
    public abstract IComponent getPendingRequestsMessageComponent();

    @Component(id = "editor", type = "rzmLib:ContactEditor",
               bindings = {"editor=prop:contactEditor", "contactAttributes=prop:contactAttributes", "validationRequired=literal:false"}
    )
    public abstract IComponent getContactAttributeEditorComponent();

    @Component(id="editContactTitle", type="Insert", bindings = {"value=prop:pageTitle"})
    public abstract IComponent getEditContactDetailsTitleComponent();

    @InjectPage(EditDomain.PAGE_NAME)
    public abstract EditDomain getEditDomain();

    @InjectPage(RequestsPerspective.PAGE_NAME)
    public abstract RequestsPerspective getRequestsPerspective();

    @Persist("client")
    public abstract void setContactType(String contactType);
    public abstract String getContactType();

    @Persist("client")
    public abstract void setDomainId(long id);
    public abstract long getDomainId();

    @Persist("client")
    public abstract ICallback getCallback();
    public abstract void setCallback(ICallback callback);

    @Persist("client")
    public abstract void setContactAttributes(Map<String, String> attributes);
    public abstract Map<String, String> getContactAttributes();

    @Persist("client")
    public abstract DomainVOWrapper getModifiedDomain();
    public abstract void setModifiedDomain(DomainVOWrapper domain);

    public abstract void setOriginalContact(ContactVOWrapper contact);
    public abstract ContactVOWrapper getOriginalContact();

    protected Object[] getExternalParameters() {
        return new Object[]{
            getDomainId(), getContactType(), getContactAttributes(), getCallback(), getModifiedDomain()
        };
    }

    public String getPageTitle(){
        return "Edit " + getContactType() + " Contact ("  + getVisitState().getCurrentDomain(getDomainId()).getName() + ")"; 
    }

    @SuppressWarnings("unchecked")
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length < 4) {
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
            return;
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
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        }
    }

    public AttributesEditor getContactEditor() {
        return this;
    }

    public void save(Map<String, String> attributes) {
        String type = getContactType();
        DomainChangeType changeType = DomainChangeType.fromString(type);

        if(!changeType.equals(DomainChangeType.SO)){

            if(!attributes.containsKey(ContactVOWrapper.NAME)){
                setErrorMessage(getMessageUtil().missingrequiredFieldMessage("Contact Name"));
            }

            if(attributes.get(ContactVOWrapper.NAME) == null){
                setErrorMessage(getMessageUtil().missingrequiredFieldMessage("Contact Name"));
            }

            if(isHasErrors()){
                return;
            }
        }


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
        page.setEntityFetcher(new OpenTransactionForDomainsRetriver(Arrays.asList(getVisitState().getCurrentDomain(
            getDomainId()).getName()), getRzmServices()));
        page.setCallback(createCallback());
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
