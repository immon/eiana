package org.iana.rzm.web.components.user;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.AccessDeniedException;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.Visit;
import org.iana.rzm.web.model.ContactVOWrapper;
import org.iana.rzm.web.model.RoleVOWrapper;
import org.iana.rzm.web.model.SystemDomainVOWrapper;
import org.iana.rzm.web.model.SystemRoleVOWrapper;
import org.iana.rzm.web.pages.user.UserGeneralError;
import org.iana.rzm.web.services.AccessDeniedHandler;
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

    @Component(id = "jobTitle", type = "RzmInsert", bindings = {
            "value=prop:jobTitle", "originalValue=prop:originalJobTitle", "modifiedStyle=literal:edited"}
    )
    public abstract IComponent getJobTitleComponent();

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

    @Component(id = "privateEmail", type = "RzmInsert", bindings = {
            "value=prop:privateEmail", "originalValue=prop:originalPrivateEmail", "modifiedStyle=literal:edited"})
    public abstract IComponent getPrivateEmailComponent();

    @Component(id = "phone", type = "RzmInsert", bindings = {
            "value=prop:phone", "originalValue=prop:originalPhone", "modifiedStyle=literal:edited"})
    public abstract IComponent getPhoneComponent();

    @Component(id = "altPhone", type = "RzmInsert", bindings = {
            "value=prop:altPhone", "originalValue=prop:altOriginalPhone", "modifiedStyle=literal:edited"})
    public abstract IComponent getAltPhoneComponent();

    @Component(id = "phoneSefix", type = "Insert", bindings = {
            "value=prop:phoneSefix", "raw=literal:true"})
    public abstract IComponent getPhoneSefixComponent();

    @Component(id = "fax", type = "RzmInsert", bindings = {
            "value=prop:fax", "originalValue=prop:originalFax", "modifiedStyle=literal:edited"})
    public abstract IComponent getFaxComponent();

    @Component(id = "altFax", type = "RzmInsert", bindings = {
            "value=prop:altFax", "originalValue=prop:altOriginalFax", "modifiedStyle=literal:edited"})
    public abstract IComponent getAltFaxComponent();

    @Component(id = "faxSefix", type = "Insert", bindings = {
            "value=prop:faxSefix", "raw=literal:true"})
    public abstract IComponent getFaxSefixComponent();

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

    @Component(id = "privateEmailSpan", type = "Any", bindings = {"element=literal:span", "class=prop:privateEmailStyle"})
    public abstract IComponent getPrivateEmailSpanComponent();

    @Component(id = "altPhoneSpan", type = "Any", bindings = {"element=literal:span", "class=prop:alternatePhoneSpan"})
    public abstract IComponent getAltPhoneSpanComponent();

    @Component(id = "altFaxSpan", type = "Any", bindings = {"element=literal:span", "class=prop:alternateFaxSpan"})
    public abstract IComponent getAltFaxSpanComponent();

    @Component(id = "jobTitleSpan", type = "Any", bindings = {"element=literal:span", "class=prop:jobTitleSpan"})
    public abstract IComponent getJobTitleSpanComponent();

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

    @InjectObject("service:rzm.AccessDeniedHandler")
    public abstract AccessDeniedHandler getAccessDeniedHandler();

    @InjectState("visit")
    public abstract Visit getVisitState();

    public abstract void setOriginalAttributes(Map<String, String> c);

    public abstract Map<String, String> getOriginalAttributes();

    public abstract void setAddress(String address);

    public abstract void setLastUpdated(String value);

    public abstract void setFax(String fax);
    public abstract String getFax();

    public abstract void setEmail(Object email);

    public abstract void setPhone(String phone);
    public abstract String getPhone();

    public abstract void setName(Object name);

    public abstract void setUserRole(String text);

    public abstract void setOrganization(String org);

    public abstract void setPrivateEmail(String privateEmail);

    public abstract void setAltPhone(String alternatePhone);

    public abstract void setAltFax(String alternateFax);
    public abstract String getAltFax();

    public abstract void setJobTitle(String job);

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
            setJobTitle(getContactAttributes().get(ContactVOWrapper.JOB_TITLE));
            setOrganization(getContactAttributes().get(ContactVOWrapper.ORGANISATION));
            setPrivateEmail(getContactAttributes().get(ContactVOWrapper.PRIVATE_EMAIL));
            setAltPhone(getContactAttributes().get(ContactVOWrapper.ALT_PHONE));
            setAltFax(getContactAttributes().get(ContactVOWrapper.ALT_FAX));
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
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        } catch (AccessDeniedException e) {
            getAccessDeniedHandler().handleAccessDenied(e, UserGeneralError.PAGE_NAME);
        }
    }

    public String getPhoneSefix() {
        if (StringUtils.isBlank(getPhone())){
            return "";
        }

        return "(v) <br/>";
    }

    public String getFaxSefix() {
        if (StringUtils.isBlank(getFax())){
            return "";
        }
        String br = StringUtils.isBlank(getAltFax()) ? "" : "<br/>";
        return "(f) " + br;
    }

    public String getAddressClass() {
        return isAddressModified() ? "edited" : "";
    }

    public String getRoleAccountClass(){
        String s = getOriginalAttributes().get(ContactVOWrapper.ROLE);
        return isRole() == Boolean.valueOf(s) ? "" : "edited";
    }

    public boolean isRole() {
        String role = getContactAttributes().get(ContactVOWrapper.ROLE);
        return Boolean.valueOf(role);
    }

    public String getAlternateFaxSpan() {
        String fax = getContactAttributes().get(ContactVOWrapper.ALT_FAX);
        String originalFax = getOriginalAttributes().get(ContactVOWrapper.ALT_FAX);
        return getAlternateStyle(fax, originalFax);
    }

    public String getAlternatePhoneSpan() {
        String phone = getContactAttributes().get(ContactVOWrapper.ALT_PHONE);
        String originalPhone = getOriginalAttributes().get(ContactVOWrapper.ALT_PHONE);
        return getAlternateStyle(phone, originalPhone);

    }

    public String getPrivateEmailStyle() {
        String email = getContactAttributes().get(ContactVOWrapper.PRIVATE_EMAIL);
        String originalEmail = getOriginalAttributes().get(ContactVOWrapper.PRIVATE_EMAIL);
        return getAlternateStyle(email, originalEmail);
    }

    public String getJobTitleSpan() {
        String jobTitle = getContactAttributes().get(ContactVOWrapper.JOB_TITLE);
        String originalJobTitle = getOriginalAttributes().get(ContactVOWrapper.JOB_TITLE);
        return getAlternateStyle(jobTitle, originalJobTitle);
    }


    public boolean isAddressModified() {
        String originalAddress = getOriginalAttributes().get(ContactVOWrapper.ADDRESS);
        String originalCountry = getOriginalAttributes().get(ContactVOWrapper.COUNTRY);
        String newAddress = getContactAttributes().get(ContactVOWrapper.ADDRESS);
        String country = getContactAttributes().get(ContactVOWrapper.COUNTRY);
        return !(StringUtils.equals(originalAddress, newAddress) && StringUtils.equals(originalCountry, country));
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

    public String getOriginalOrg() {
        return getOriginalAttributes().get(ContactVOWrapper.ORGANISATION);
    }

    public String getAltOriginalPhone() {
        return getOriginalAttributes().get(ContactVOWrapper.ALT_PHONE);
    }

    public String getOriginalPrivateEmail() {
        return getOriginalAttributes().get(ContactVOWrapper.PRIVATE_EMAIL);
    }

    public String getAltOriginalFax() {
        return getOriginalAttributes().get(ContactVOWrapper.ALT_FAX);
    }

    public String getOriginalJobTitle() {
        return getOriginalAttributes().get(ContactVOWrapper.JOB_TITLE);
    }

    public boolean isUsePrivateEmail() {
        return !getPrivateEmailStyle().equals("hidden");
    }

    public boolean isUseAltPhone() {
        return !getAlternatePhoneSpan().equals("hidden");
    }

    public boolean isUseJobTitle() {
        return !getJobTitleSpan().equals("hidden");
    }


    private String getAlternateStyle(String value, String originalValue) {
        if (StringUtils.equals(value, originalValue) && StringUtils.isBlank(value)) {
            return "hidden";
        }

        if (StringUtils.equals(value, originalValue)) {
            return "";
        }

        return "edited";
    }


    private String buildAddress(Map<String, String> attributes) {
        StringBuilder builder = new StringBuilder();
        String address = attributes.get(ContactVOWrapper.ADDRESS);
        builder.append(address).append(" ");
        String country = attributes.get(ContactVOWrapper.COUNTRY);
        if(!StringUtils.isEmpty(country)){
            builder.append(country);
        }

        return builder.toString();
    }

}