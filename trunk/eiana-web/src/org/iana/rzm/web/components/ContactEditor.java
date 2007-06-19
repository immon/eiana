package org.iana.rzm.web.components;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.validator.EmailValidator;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.valid.IValidationDelegate;
import org.iana.rzm.web.common.ContactAttributesEditor;
import org.iana.rzm.web.model.ContactVOWrapper;
import org.iana.rzm.web.services.user.UserServices;
import org.iana.rzm.web.util.WebUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;


@ComponentClass
public abstract class ContactEditor extends BaseComponent implements PageBeginRenderListener {

    @Component(id = "editContact", type = "Form", bindings = {
            "clientValidationEnabled=literal:false",
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
            "displayName=message:alt-email-label", "value=prop:privateEmail"})
    public abstract IComponent getPrivateEmailComponent();

    @Component(id = "phone", type = "TextField", bindings = {
            "displayName=message:phone-label", "value=ognl:contactAttributes.PHONE", "validators=validators:required"})
    public abstract IComponent getPhoneComponent();

    @Component(id = "altPhone", type = "TextField", bindings = {
            "displayName=message:alt-phone-label", "value=prop:alternatePhone"})
    public abstract IComponent getAltPhoneComponent();

    @Component(id = "fax", type = "TextField", bindings = {
            "displayName=message:fax-label", "value=ognl:contactAttributes.FAX"})
    public abstract IComponent getFaxComponent();

    @Component(id = "altFax", type = "TextField", bindings = {
            "displayName=message:alt-fax-label", "value=prop:alternateFax"})
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

    @Component(id = "phoneTR", type = "Any", bindings = {"style=prop:altPhoneClass"})
    public abstract IComponent getAltPhoneTRComponent();

    @Component(id = "faxTR", type = "Any", bindings = {"style=prop:altFaxClass"})
    public abstract IComponent getAltFaxTRComponent();

    @Component(id = "emailTR", type = "Any", bindings = {"style=prop:privateEmailClass"})
    public abstract IComponent getPrivateEmailTRComponent();

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
    public abstract IFormComponent getPrivateEmailField();

    @InjectComponent("jobTitle")
    public abstract IFormComponent getJobTitleField();

    @InjectObject("service:tapestry.globals.HttpServletRequest")
    public abstract HttpServletRequest getHttpRequest();

    @InjectObject("service:rzm.UserServices")
    public abstract UserServices getUserServices();

    public boolean getRole() {
        String role = getContactAttributes().get(ContactVOWrapper.ROLE);
        return Boolean.valueOf(role);
    }

    public void setRole(boolean value) {
        String role = String.valueOf(value);
        getContactAttributes().put(ContactVOWrapper.ROLE, role);
    }

    public abstract void setUsePrivateEmail(boolean value);

    public abstract boolean isUsePrivateEmail();

    public abstract void setAddAltPhone(boolean value);

    public abstract boolean isAddAltPhone();

    public abstract void setAddAltFax(boolean value);

    public abstract boolean isAddAltFax();

    public abstract String getPrivateEmail();

    public abstract void setPrivateEmail(String email);

    public abstract String getAlternateFax();

    public abstract void setAlternateFax(String fax);

    public abstract String getAlternatePhone();

    public abstract void setAlternatePhone(String phone);

    public void pageBeginRender(PageEvent event) {

        if (!event.getRequestCycle().isRewinding()) {

            if (StringUtils.isBlank(getPrivateEmail()) && (!isUsePrivateEmail())) {
                setPrivateEmail(getContactAttributes().get(ContactVOWrapper.PRIVATE_EMAIL));
                setUsePrivateEmail(StringUtils.isNotBlank(getPrivateEmail()));
            }

            if (StringUtils.isBlank(getAlternateFax()) && (!isAddAltFax())) {
                setAlternateFax(getContactAttributes().get(ContactVOWrapper.ALT_FAX));
                setAddAltFax(StringUtils.isNotBlank(getAlternateFax()));
            }

            if (StringUtils.isBlank(getAlternatePhone()) && (!isAddAltPhone())) {
                setAlternatePhone(getContactAttributes().get(ContactVOWrapper.ALT_PHONE));
                setAddAltPhone(StringUtils.isNotBlank(getAlternatePhone()));
            }

        } else {

            if (!isUsePrivateEmail()) {
                setPrivateEmail(null);
            }

            if (!isAddAltFax()) {
                setAlternateFax(null);
            }

            if (!isAddAltPhone()) {
                setAlternatePhone(null);
            }
        }

    }

    public void revert() {
        getEditor().revert();
    }

    public void save() {
        getEditor().preventResubmission();

        Map<String, String> attributes = getContactAttributes();

        validateInput();
        if (getEditor().getValidationDelegate().getHasErrors()) {
            return;
        }

        if (isUsePrivateEmail()) {
            attributes.put(ContactVOWrapper.PRIVATE_EMAIL, getPrivateEmail());
        }

        if (isAddAltFax()) {
            attributes.put(ContactVOWrapper.ALT_FAX, getAlternateFax());
        }

        if (isAddAltPhone()) {
            attributes.put(ContactVOWrapper.ALT_PHONE, getAlternatePhone());
        }

        getEditor().save(getContactAttributes());
    }

    public IValidationDelegate getValidationDelegate() {
        return getEditor().getValidationDelegate();
    }

    public String getDisplayStyle(boolean visible) {
        if (visible) {
            return WebUtil.isIEBrowser(getHttpRequest()) ? "display:inline" : "table-row";
        }
        return "display:none";
    }

    public String getPrivateEmailClass() {
        return getDisplayStyle(isUsePrivateEmail());
    }

    public String getAltPhoneClass() {
        return getDisplayStyle(isAddAltPhone());
    }

    public String getAltFaxClass() {
        return getDisplayStyle(isAddAltFax());
    }

    private void validateInput() {
        Map<String, String> contactAttributes = getContactAttributes();
        validateReqiredField(contactAttributes, ContactVOWrapper.NAME, getNameField());
        validateReqiredField(contactAttributes, ContactVOWrapper.ORGANISATION, getOrganisationField());
        validateReqiredField(contactAttributes, ContactVOWrapper.EMAIL, getEmailField());
        validateReqiredField(contactAttributes, ContactVOWrapper.PHONE, getPhoneField());
        validateReqiredField(contactAttributes, ContactVOWrapper.ADDRESS, getAddressField());
        validateReqiredField(contactAttributes, ContactVOWrapper.COUNTRY, getCountryField());

        validateCountryCode(contactAttributes.get(ContactVOWrapper.COUNTRY));

        if (isUsePrivateEmail()) {
            validateEmail(getPrivateEmail(), getPrivateEmailField(), ContactVOWrapper.PRIVATE_EMAIL);
        }

        if (getRole()) {
            if (StringUtils.isBlank(contactAttributes.get(ContactVOWrapper.JOB_TITLE))) {
                getEditor().setErrorField(getJobTitleField(), "Role accounts must have a Job Title");
            }
        }
    }

    private void validateCountryCode(String code) {
        if (code != null) {
            if (!getUserServices().isValidCountryCode(code)) {
                getEditor().setErrorField(getCountryField(), "Invalid Country Code " + code);
            }
        }
    }

    private void validateEmail(String email, IFormComponent field, String fieldName) {
        EmailValidator validator = EmailValidator.getInstance();

        if (StringUtils.isBlank(email)) {
            getEditor().setErrorField(field, "Please specify value for " + fieldName);
            return;
        }

        if (!validator.isValid(email)) {
            getEditor().setErrorField(field, "Invalid email address format " + email);
        }
    }

    private void validateReqiredField(Map attributes, String fieldName, IFormComponent field) {
        String value = (String) attributes.get(fieldName);
        validateField(value, field, fieldName);
    }

    private void validateField(String value, IFormComponent field, String fieldName) {
        if (StringUtils.isBlank(value)) {
            getEditor().setErrorField(field, "Please specify value for " + fieldName);
        }
    }

}
