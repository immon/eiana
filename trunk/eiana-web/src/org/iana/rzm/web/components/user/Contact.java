package org.iana.rzm.web.components.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.Visit;
import org.iana.rzm.web.model.ContactVOWrapper;
import org.iana.rzm.web.model.RoleVOWrapper;
import org.iana.rzm.web.model.SystemDomainVOWrapper;
import org.iana.rzm.web.model.SystemRoleVOWrapper;
import org.iana.rzm.web.services.ObjectNotFoundHandler;
import org.iana.rzm.web.services.user.UserServices;

import java.util.List;
import java.util.Map;

@ComponentClass
public abstract class Contact extends BaseComponent {

    @Component(id = "type", type = "Insert", bindings = {"value=prop:type"})
    public abstract IComponent getTypeComponent();

    @Component(id = "contactName", type = "RzmInsert", bindings = {
            "value=prop:name", "originalValue=prop:originalName", "modifiedStyle=literal:edited"}
    )
    public abstract IComponent getContactNameComponent();

    @Component(id = "org", type = "RzmInsert", bindings = {
            "value=prop:organization", "originalValue=prop:originalOrg", "modifiedStyle=literal:edited"}
    )
    public abstract IComponent getContactOrganizationComponent();

    @Component(id = "address", type = "InsertText", bindings = {
            "value=prop:address", "raw=literal:true", "mode=@org.apache.tapestry.html.InsertTextMode@PARAGRAPH"})
    public abstract IComponent getAddressComponent();

    @Component(id = "email", type = "RzmInsert", bindings = {
            "value=prop:email", "originalValue=prop:originalEmail", "modifiedStyle=literal:edited"})
    public abstract IComponent getEmailComponent();

    @Component(id = "phone", type = "RzmInsert", bindings = {
            "value=prop:phone", "originalValue=prop:originalPhone", "modifiedStyle=literal:edited"})
    public abstract IComponent getPhoneComponent();

    @Component(id = "fax", type = "RzmInsert", bindings = {
            "value=prop:fax", "originalValue=prop:originalFax", "modifiedStyle=literal:edited"})
    public abstract IComponent getFaxComponent();

    @Component(id = "lastUpdated", type = "Insert", bindings = {"value=prop:lastUpdated"})
    public abstract IComponent getLastUpdatedComponent();

    @Component(id = "edit", type = "DirectLink", bindings = {
            "listener=prop:listener", "parameters=prop:editParameters",
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getEditLinkComponent();

    @Component(id = "editible", type = "If", bindings = {"condition=prop:editible"})
    public abstract IComponent getEditibleComponent();

    @Component(id = "userRole", type = "Insert", bindings = {"value=prop:userRole", "raw=literal:true"})
    public abstract IComponent getUserRoleComponent();

    @Asset(value = "WEB-INF/user/Contact.html")
    public abstract IAsset get$template();

    @Parameter(required = true)
    public abstract Map<String, String> getContactAttributes();

    public abstract void setContactAttributes(Map<String, String> attributes);

    @Parameter(required = true)
    public abstract long getDomainId();

    @Parameter(required = true)
    public abstract String getType();

    @Parameter(required = false, defaultValue = "true")
    public abstract boolean isEditible();

    public abstract void setEditible(boolean value);

    @Parameter(required = false)
    public abstract IActionListener getListener();

    @InjectObject("service:rzm.UserServices")
    public abstract UserServices getUserServices();

    @InjectObject("service:rzm.ObjectNotFoundHandler")
    public abstract ObjectNotFoundHandler getObjectNotFoundHandler();

    @InjectState("visit")
    public abstract Visit getVisitState();

    public abstract void setOriginalAttributes(Map<String, String> c);

    public abstract Map<String, String> getOriginalAttributes();

    public abstract void setAddress(String address);

    public abstract void setLastUpdated(String value);

    public abstract void setFax(String fax);

    public abstract void setEmail(Object email);

    public abstract void setPhone(String phone);

    public abstract void setName(Object name);

    public abstract void setUserRole(String text);

    public abstract void setOrganization(String org);

    public Object[] getEditParameters() {
        String contactId = getContactAttributes().get(ContactVOWrapper.ID);
        CheckTool.checkNull(contactId, ContactVOWrapper.ID);
        Long id = Long.parseLong(contactId);
        return new Object[]{id, getType()};
    }
                                           
    protected void renderComponent(IMarkupWriter writer, IRequestCycle cycle) {

        if (!cycle.isRewinding()) {
            setAddress(buildAddress(getContactAttributes()));
            setLastUpdated(getContactAttributes().get(ContactVOWrapper.LAST_UPDATED));
            setPhone(getContactAttributes().get(ContactVOWrapper.PHONE));
            setFax(getContactAttributes().get(ContactVOWrapper.FAX));
            setEmail(getContactAttributes().get(ContactVOWrapper.EMAIL));
            setName(getContactAttributes().get(ContactVOWrapper.NAME));
            setOrganization(getContactAttributes().get(ContactVOWrapper.ORGANISATION));
        }

        try {
            SystemDomainVOWrapper domain = (SystemDomainVOWrapper) getUserServices().getDomain(getDomainId());
            long id = Long.parseLong(getContactAttributes().get(ContactVOWrapper.ID));
            setOriginalAttributes(domain.getContact(id, getType()).getMap());
            setUserRole("");
            List<SystemRoleVOWrapper> roles = domain.getRoles();

            for (RoleVOWrapper role : roles) {
                if (role.getType().serverName().equals(getType())) {
                    setUserRole("<br><span class=\"grey small-text\">(this is you)</span>");
                }
            }

            super.renderComponent(writer, cycle);
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e);
        }
    }

    public String getAddressClass() {
        return isAddressModified() ? "edited" : "";
    }

    public boolean isAddressModified() {
        String originalAddress = getOriginalAttributes().get(ContactVOWrapper.ADDRESS);
        String newAddress = getContactAttributes().get(ContactVOWrapper.ADDRESS);
        return originalAddress != null && (!originalAddress.equals(newAddress));
    }

    public String getOriginalAddress() {
        return buildAddress(getOriginalAttributes());
    }

    public String getOriginalEmail() {
        return getOriginalAttributes().get(ContactVOWrapper.EMAIL);
    }

    public String getOriginalFax() {
        return getOriginalAttributes().get(ContactVOWrapper.FAX);
    }

    public String getOriginalPhone() {
        return getOriginalAttributes().get(ContactVOWrapper.PHONE);
    }

    public String getOriginalName() {
        return getOriginalAttributes().get(ContactVOWrapper.NAME);
    }

    public String getOriginalOrg(){
        return getOriginalAttributes().get(ContactVOWrapper.ORGANISATION);
    }

    private String buildAddress(Map<String, String> attributes) {
        StringBuilder builder = new StringBuilder();
        builder.append(attributes.get(ContactVOWrapper.ADDRESS)).append(" ");
        builder.append(attributes.get(ContactVOWrapper.COUNTRY));

        return builder.toString();
    }

}