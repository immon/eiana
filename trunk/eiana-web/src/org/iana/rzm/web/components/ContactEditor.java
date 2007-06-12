package org.iana.rzm.web.components;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.Parameter;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.valid.IValidationDelegate;
import org.iana.rzm.web.common.ContactAttributesEditor;
import org.iana.rzm.web.model.ContactVOWrapper;

import java.util.Map;


@ComponentClass
public abstract class ContactEditor extends BaseComponent implements PageBeginRenderListener {

    @Component(id = "editContact", type = "Form", bindings = {
            "clientValidationEnabled=literal:true",
            "success=listener:save",
            //"cancel=listener:revert",
            "delegate=prop:validationDelegate"
            })
    public abstract IComponent getEditContactComponent();

    @Component(id = "name", type = "TextField", bindings = {
            "displayName=message:name-label", "value=ognl:contactAttributes.NAME", "validators=validators:required"})
    public abstract IComponent getNameComponent();

    @Component(id = "jobTitle", type = "TextField", bindings = {
            "displayName=message:job-title-label", "value=ognl:contactAttributes.JOB_TITLE"})
    public abstract IComponent getJobTitleComponent();

    @Component(id = "organisation", type = "TextField", bindings = {
            "displayName=message:organisation-label", "value=ognl:contactAttributes.ORGANIZATION"})
    public abstract IComponent getOrganisationComponent();

    @Component(id = "address", type = "TextArea", bindings = {
            "displayName=message:address-label", "value=ognl:contactAttributes.ADDRESS"})
    public abstract IComponent getStreetComponent();

    @Component(id = "country", type = "TextField", bindings = {
            "displayName=message:country-label", "value=ognl:contactAttributes.COUNTRY", "validators=validators:required"})
    public abstract IComponent getCountryComponent();

    @Component(id = "email", type = "TextField", bindings = {
            "displayName=message:email-label", "value=ognl:contactAttributes.EMAIL", "validators=validators:required, email"})
    public abstract IComponent getEmailComponent();

    @Component(id = "privateEmail", type = "TextField", bindings = {
            "displayName=message:alt-email-label", "value=ognl:contactAttributes.PRIVATE_EMAIL", "validators=validators:email"})
    public abstract IComponent getPrivateEmailComponent();

    @Component(id = "phone", type = "TextField", bindings = {
            "displayName=message:phone-label", "value=ognl:contactAttributes.PHONE", "validators=validators:required"})
    public abstract IComponent getPhoneComponent();

    @Component(id = "altPhone", type = "TextField", bindings = {
            "displayName=message:alt-phone-label", "value=ognl:contactAttributes.ALT_PHONE"})
    public abstract IComponent getAltPhoneComponent();

    @Component(id = "fax", type = "TextField", bindings = {
            "displayName=message:fax-label", "value=ognl:contactAttributes.FAX"})
    public abstract IComponent getFaxComponent();

    @Component(id = "altFax", type = "TextField", bindings = {
            "displayName=message:alt-fax-label", "value=ognl:contactAttributes.ALT_FAX"})
    public abstract IComponent getAltFaxComponent();

    @Component(id = "role", type = "Checkbox", bindings = {
            "displayName=message:role-label", "value=prop:role"})
    public abstract IComponent getRoleComponent();

    @Component(id = "roleLabel", type = "FieldLabel", bindings = {"field=component:role"})
    public abstract IComponent getRoleLabelComponent();

    @Component(id = "save", type = "LinkSubmit")
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @Component(id = "privateEmailCheckbox", type = "Checkbox", bindings = {"value=prop:usePrivateEmail"})
    public abstract IComponent getPrivateEmailCheckBoxComponent();

    @Component(id = "altPhoneCheckbox", type = "Checkbox", bindings = {"value=prop:addAltPhone"})
    public abstract IComponent getAltPhoneCheckBoxComponent();

    @Component(id = "altFaxCheckbox", type = "Checkbox", bindings = {"value=prop:addAltFax"})
    public abstract IComponent getAltFaxCheckBoxComponent();

    @Parameter(required = true)
    public abstract ContactAttributesEditor getEditor();

    @Parameter(required = true)
    public abstract Map<String, String> getContactAttributes();

    @InjectComponent("name")
    public abstract IFormComponent getNameField();

    @InjectComponent("organisation")
    public abstract IFormComponent getOrganisationField();

    @InjectComponent("country")
    public abstract IFormComponent getCountryField();

    @InjectComponent("phone")
    public abstract IFormComponent getPhoneField();

    @InjectComponent("fax")
    public abstract IFormComponent getFaxField();

    @InjectComponent("email")
    public abstract IFormComponent getEmailField();

    @InjectComponent("address")
    public abstract IFormComponent getAddressField();

    @InjectComponent("privateEmail")
    public abstract IFormComponent getPrivateEmail();

    public abstract boolean getRole();

    public abstract void setRole(boolean role);

    public abstract void setUsePrivateEmail(boolean value);

    public abstract boolean isUsePrivateEmail();

    public abstract void setAddAltPhone(boolean value);
    public abstract boolean isAddAltPhone();

    public abstract void setAddAltFax(boolean value);
    public abstract boolean isAddAltFax();

    public void pageBeginRender(PageEvent event) {
        String role = getContactAttributes().get(ContactVOWrapper.ROLE);
        String privateEmail = getContactAttributes().get(ContactVOWrapper.PRIVATE_EMAIL);
        String altPhone = getContactAttributes().get(ContactVOWrapper.ALT_PHONE);
        String altFax = getContactAttributes().get(ContactVOWrapper.ALT_FAX);

        if (!event.getRequestCycle().isRewinding()) {
            setRole(Boolean.valueOf(role));
            setUsePrivateEmail(StringUtils.isNotBlank(privateEmail));
            setAddAltPhone(StringUtils.isNotBlank(altPhone));
            setAddAltFax(StringUtils.isNotBlank(altFax));
        }
    }

    public void revert() {
        getEditor().revert();
    }

    public void save() {
        getEditor().preventResubmission();
        validateInput();
        if (getEditor().getValidationDelegate().getHasErrors()) {
            return;
        }

        getContactAttributes().put(ContactVOWrapper.ROLE, String.valueOf(getRole()));

        getEditor().save(getContactAttributes());
    }

    public IValidationDelegate getValidationDelegate() {
        return getEditor().getValidationDelegate();
    }

    public String getPrivateEmailClass(){
        if(isUsePrivateEmail()){
            return "";
        }

        return "hidden";
    }

    public String getAltPhoneClass(){
        if(isAddAltPhone()){
            return "";
        }

        return "hidden";
    }

    public String getAltFaxClass(){
        if(isAddAltFax()){
            return "";
        }

        return "hidden";
    }

    private void validateInput() {
        Map<String, String> contactAttributes = getContactAttributes();
        validateReqiredField(contactAttributes, ContactVOWrapper.NAME, getNameField());
        validateReqiredField(contactAttributes, ContactVOWrapper.ORGANISATION, getOrganisationField());
        validateReqiredField(contactAttributes, ContactVOWrapper.EMAIL, getEmailField());
        validateReqiredField(contactAttributes, ContactVOWrapper.PHONE, getPhoneField());
        validateReqiredField(contactAttributes, ContactVOWrapper.ADDRESS, getAddressField());
        validateReqiredField(contactAttributes, ContactVOWrapper.COUNTRY, getCountryField());

        if(isUsePrivateEmail()){
            validateReqiredField(contactAttributes, ContactVOWrapper.PRIVATE_EMAIL, getPrivateEmail());
        }
    }

    private void validateReqiredField(Map attributes, String fieldName, IFormComponent field) {

        String value = (String) attributes.get(fieldName);
        if (StringUtils.isBlank(value)) {
            getEditor().setErrorField(field, "Please specify value for " + fieldName);
        }
    }

}
