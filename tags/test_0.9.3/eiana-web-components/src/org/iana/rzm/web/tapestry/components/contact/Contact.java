package org.iana.rzm.web.tapestry.components.contact;

import org.apache.commons.lang.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.commons.*;
import org.iana.rzm.common.validators.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.pages.*;

import java.util.*;

public abstract class Contact extends BaseComponent {

    @Component(id = "type", type = "Insert", bindings = {"value=prop:type"})
    public abstract IComponent getTypeComponent();

    @Component(id = "contactName", type = "tapestry4lib:TrackableInsert", bindings = {
        "value=prop:contactNameOrgValue", "raw=literal:true", "originalValue=prop:originalContactNameOrgValue", "modifiedStyle=literal:edited"}
    )
    public abstract IComponent getContactNameComponent();

    @Component(id = "jobTitle", type = "tapestry4lib:TrackableInsert", bindings = {
        "value=prop:jobTitle", "originalValue=prop:originalJobTitle", "modifiedStyle=literal:edited"}
    )
    public abstract IComponent getJobTitleComponent();

    @Component(id = "org", type = "tapestry4lib:TrackableInsert", bindings = {
        "value=prop:organization", "raw=literal:true", "originalValue=prop:originalOrg", "modifiedStyle=literal:edited"}
    )
    public abstract IComponent getContactOrganizationComponent();

    @Component(id = "address", type = "InsertText", bindings = {
        "value=prop:address", "raw=literal:true", "mode=@org.apache.tapestry.html.InsertTextMode@PARAGRAPH"})
    public abstract IComponent getAddressComponent();

    @Component(id = "country", type = "tapestry4lib:TrackableInsert", bindings = {
        "value=prop:country", "raw=literal:true", "originalValue=prop:originalCountry", "modifiedStyle=literal:edited"}
    )
    public abstract IComponent getCountryComponent();


    @Component(id = "email", type = "tapestry4lib:TrackableInsert", bindings = {
        "value=prop:email", "originalValue=prop:originalEmail", "modifiedStyle=literal:edited"})
    public abstract IComponent getEmailComponent();

    @Component(id = "privateEmail", type = "tapestry4lib:TrackableInsert", bindings = {
        "value=prop:privateEmail", "originalValue=prop:originalPrivateEmail", "modifiedStyle=literal:edited"})
    public abstract IComponent getPrivateEmailComponent();

    @Component(id = "phone", type = "tapestry4lib:TrackableInsert", bindings = {
        "value=prop:phone", "originalValue=prop:originalPhone", "modifiedStyle=literal:edited"})
    public abstract IComponent getPhoneComponent();

    @Component(id = "altPhone", type = "tapestry4lib:TrackableInsert", bindings = {
        "value=prop:altPhone", "originalValue=prop:altOriginalPhone", "modifiedStyle=literal:edited"})
    public abstract IComponent getAltPhoneComponent();

    @Component(id = "phoneSefix", type = "tapestry4lib:TrackableInsert", bindings = {
        "value=prop:phoneSefix", "raw=literal:true"})
    public abstract IComponent getPhoneSefixComponent();

    @Component(id = "fax", type = "tapestry4lib:TrackableInsert", bindings = {
        "value=prop:fax", "originalValue=prop:originalFax", "modifiedStyle=literal:edited"})
    public abstract IComponent getFaxComponent();

    @Component(id = "altFax", type = "tapestry4lib:TrackableInsert", bindings = {
        "value=prop:altFax", "originalValue=prop:altOriginalFax", "modifiedStyle=literal:edited"})
    public abstract IComponent getAltFaxComponent();

    @Component(id = "faxSefix", type = "Insert", bindings = {
        "value=prop:faxSefix", "raw=literal:true"})
    public abstract IComponent getFaxSefixComponent();

    @Component(id = "lastUpdated", type = "Insert", bindings = {"value=prop:lastUpdated"})
    public abstract IComponent getLastUpdatedComponent();

    @Component(id = "edit", type = "DirectLink", bindings = {
        "listener=prop:listener", "parameters=prop:editParameters",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getEditLinkComponent();

    @Component(id = "editible", type = "If", bindings = {"condition=prop:editible"})
    public abstract IComponent getEditibleComponent();

    @Component(id = "userRole", type = "Insert", bindings = {"value=prop:userRole", "raw=literal:true"})
    public abstract IComponent getUserRoleComponent();

    @Component(id = "privateEmailSpan",
               type = "Any",
               bindings = {"element=literal:span", "class=prop:privateEmailStyle"})
    public abstract IComponent getPrivateEmailSpanComponent();

    @Component(id = "altPhoneSpan", type = "Any", bindings = {"element=literal:span", "class=prop:alternatePhoneSpan"})
    public abstract IComponent getAltPhoneSpanComponent();

    @Component(id = "altFaxSpan", type = "Any", bindings = {"element=literal:span", "class=prop:alternateFaxSpan"})
    public abstract IComponent getAltFaxSpanComponent();

    @Component(id = "jobTitleSpan", type = "Any", bindings = {"element=literal:span", "class=prop:jobTitleSpan"})
    public abstract IComponent getJobTitleSpanComponent();

    public abstract ContactServices getContactServices();
    public abstract Map<String, String> getContactAttributes();
    public abstract void setContactAttributes(Map<String, String> attributes);
    public abstract long getDomainId();
    public abstract String getType();
    public abstract BaseGeneralError getErrorPage();
    public abstract boolean isEditible();
    public abstract IActionListener getListener();
    public abstract boolean isNew();

    public abstract void setOriginalAttributes(Map<String, String> c);
    public abstract Map<String, String> getOriginalAttributes();

    public abstract void setAddress(String address);
    public abstract void setCountry(String country) ;

    public abstract void setLastUpdated(String value);
    public abstract void setEditible(boolean value);

    public abstract void setFax(String fax);
    public abstract String getFax();

    public abstract void setEmail(Object email);

    public abstract void setPhone(String phone);
    public abstract String getPhone();

    public abstract void setName(Object name);
    public abstract Object getName();

    public abstract void setUserRole(String text);

    public abstract void setOrganization(String org);
    public abstract String getOrganization();

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
            setCountry(getContactAttributes().get(ContactVOWrapper.COUNTRY));
            setLastUpdated("Last updated " + getContactAttributes().get(ContactVOWrapper.LAST_UPDATED));
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
            if(!isNew()){
                SystemDomainVOWrapper domain = (SystemDomainVOWrapper) getContactServices().getDomain(getDomainId());
                long id = Long.parseLong(getContactAttributes().get(ContactVOWrapper.ID));
                setOriginalAttributes(domain.getContact(id, getType()).getMap());
                setUserRole("");
                List<SystemRoleVOWrapper> roles = domain.getRoles();
                for (RoleVOWrapper role : roles) {
                    if (role.getType().serverName().equals(getType())) {
                        setUserRole("<br><span class=\"grey small-text\">(this is you)</span>");
                    }
                }
            } else{
                setOriginalAttributes(new HashMap<String, String>());
            }

            super.renderComponent(writer, cycle);
        } catch (NoObjectFoundException e) {
            getContactServices().handleObjectNotFound(e, getErrorPage().getPageName());
        } catch (AccessDeniedException e) {
            getContactServices().handleAccessDenied(e, getErrorPage().getPageName());
        }
    }



    protected String buildAddress(Map<String, String> attributes) {
        StringBuilder builder = new StringBuilder();
        String address = attributes.get(ContactVOWrapper.ADDRESS);
        builder.append(address).append(" ");
        return builder.toString();
    }

    public String getPhoneSefix() {
        if (StringUtils.isBlank(getPhone())) {
            return "";
        }

        return "(v) <br/>";
    }

    public String getFaxSefix() {
        if (StringUtils.isBlank(getFax())) {
            return "";
        }
        String br = StringUtils.isBlank(getAltFax()) ? "" : "<br/>";
        return "(f) " + br;
    }

    public String getAddressClass() {
        return isAddressModified() ? "edited" : "";
    }

    public String getRoleAccountClass() {
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
        String newAddress = getContactAttributes().get(ContactVOWrapper.ADDRESS);
        return originalAddress == null || !StringUtil.equals(originalAddress.replace("\r\n", "\n"), newAddress.replace("\r\n", "\n"));
    }

    public String getOriginalAddress() {
        return buildAddress(getOriginalAttributes());
    }

    public String getOriginalCountry(){
        return getOriginalAttributes().get(ContactVOWrapper.COUNTRY);
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

    public String getContactNameOrgValue(){
        return SystemRoleVOWrapper.SUPPORTING_ORGANIZATION.equals(getType()) ? getOrganization() : getName().toString();
    }

    public String getOriginalContactNameOrgValue(){
        return SystemRoleVOWrapper.SUPPORTING_ORGANIZATION.equals(getType()) ? getOriginalOrg() : getOriginalName();
    }

    public boolean isTechOrAdminContact(){
        return  !SystemRoleVOWrapper.SUPPORTING_ORGANIZATION.equals(getType());   
    }


    private String getAlternateStyle(String value, String originalValue) {

        if(StringUtils.isBlank(value) && StringUtils.isBlank(originalValue)){
            return "hidden";
        }

        if (StringUtils.equals(value, originalValue) && StringUtils.isBlank(value)) {
            return "hidden";
        }

        if (StringUtils.equals(value, originalValue)) {
            return "";
        }

        return "edited";
    }

}
