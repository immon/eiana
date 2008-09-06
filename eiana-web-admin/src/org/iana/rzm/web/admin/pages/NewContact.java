package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.editors.*;

import java.util.*;

public abstract class NewContact extends AdminPage implements ContactAttributesEditor, IExternalPage {

    public static final String PAGE_NAME = "NewContact";

    @Component(id = "editor", type = "rzmLib:ContactEditor",
               bindings = {"editor=prop:contactEditor", "contactAttributes=prop:contactAttributes", "validationRequired=literal:false"}
    )
    public abstract IComponent getContactAttributeEditorComponent();

    @Component(id="contactType", type="Insert", bindings = {"value=prop:contactType"})
    public abstract IComponent getContactTypeComponent();

    @InjectPage(CreateDomain.PAGE_NAME)
    public abstract CreateDomain getCreateDomain();

    @Persist("client")
    public abstract void setContactType(String contactType);
    public abstract String getContactType();

    @Persist("client")
    public abstract ICallback getCallback();
    public abstract void setCallback(ICallback callback);

    @Persist("client")
    public abstract void setOriginalContact(ContactVOWrapper contact);
    public abstract ContactVOWrapper getOriginalContact();

    @Persist("client")
    public abstract void setContactAttributes(Map<String, String> attributes);
    public abstract Map<String, String> getContactAttributes();

    @Persist("client")
    public abstract DomainVOWrapper getModifiedDomain();
    public abstract void setModifiedDomain(DomainVOWrapper domain);

    @Persist("client")
    public abstract void setDomainId(long id);
    public abstract long getDomainId();


    protected Object[] getExternalParameters() {
        return new Object[]{
            getContactType(), getContactAttributes(), getCallback(), getModifiedDomain()
        };
    }

    @SuppressWarnings("unchecked")
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        //try{
        //if (parameters.length < 4) {
        //    getExternalPageErrorHandler().handleExternalPageError(
        //        getMessageUtil().getSessionRestorefailedMessage());
        //}
        //
        //} catch (NoObjectFoundException e) {
        //    getExternalPageErrorHandler().handleExternalPageError(
        //        getMessageUtil().getSessionRestorefailedMessage());
        //}
    
    }


    public void pageBeginRender(PageEvent event) {
        setModifiedDomain(getVisitState().getModifiedDomain(getDomainId()));

        if (getContactAttributes() == null) {
            setContactAttributes(new HashMap<String, String>());
        }


        if (getOriginalContact() == null) {
            setOriginalContact(getVisitState().getCurrentDomain(getDomainId()).getContact(getDomainId(), getContactType()));
        }

    }

    public AttributesEditor getContactEditor() {
        return this;
    }

    public void save(Map<String, String> attributes) {
        String type = getContactType();

        if (attributes.equals(getOriginalContact().getMap())) {
            getCallback().performCallback(getRequestCycle());
            return;
        }

        attributes.put(ContactVOWrapper.ID, "0");

        DomainChangeType changeType = DomainChangeType.fromString(type);

        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        domain.updateContactAttributes(attributes, type);
        getVisitState().markDomainDirty(getDomainId(), changeType);
        getVisitState().storeDomain(domain);
        getCallback().performCallback(getRequestCycle());
    }

    public void revert() {
        getCallback().performCallback(getRequestCycle());
    }


}
