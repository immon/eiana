package org.iana.rzm.web.components;

import org.apache.commons.lang.*;
import org.apache.commons.validator.EmailValidator;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.valid.*;
import org.iana.codevalues.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.user.*;
import org.iana.rzm.web.util.*;

import javax.servlet.http.*;
import java.io.*;
import java.util.*;


@ComponentClass
public abstract class ContactEditor extends BaseComponent implements PageBeginRenderListener {
    public static final Value NO_COUNTRY = new Value("NONE", "Country");

    @Component(id = "editContact", type = "Form", bindings = {
            "clientValidationEnabled=literal:false",
            "success=listener:save",
            "delegate=prop:validationDelegate"
     })
    public abstract IComponent getEditContactComponent();

    @Component(id = "name", type = "TextField", bindings = {
            "displayName=message:name-label", "value=prop:contactName"})
    public abstract IComponent getNameComponent();

    @Component(id = "jobTitle", type = "TextField", bindings = {
            "displayName=message:job-title-label", "value=ognl:contactAttributes.JOB_TITLE"})
    public abstract IComponent getJobTitleComponent();

    @Component(id = "organisation", type = "TextField", bindings = {
            "displayName=message:organisation-label", "value=prop:orgName"})
    public abstract IComponent getOrganisationComponent();

    @Component(id = "address", type = "TextArea", bindings = {"displayName=message:address-label", "value=ognl:contactAttributes.ADDRESS"})
    public abstract IComponent getStreetComponent();

        @Component(id = "email", type = "TextField", bindings = {"displayName=message:email-label", "value=ognl:contactAttributes.EMAIL", "validators=validators:email"})
    public abstract IComponent getEmailComponent();

    @Component(id = "privateEmail", type = "TextField", bindings = {"displayName=message:alt-email-label", "value=prop:privateEmail", "validators=validators:email"})
    public abstract IComponent getPrivateEmailComponent();

    @Component(id = "phone", type = "TextField", bindings = {"displayName=message:phone-label", "value=ognl:contactAttributes.PHONE"})
    public abstract IComponent getPhoneComponent();

    @Component(id = "altPhone", type = "TextField", bindings = {"displayName=message:alt-phone-label", "value=prop:alternatePhone"})
    public abstract IComponent getAltPhoneComponent();

    @Component(id = "fax", type = "TextField", bindings = {"displayName=message:fax-label", "value=ognl:contactAttributes.FAX"})
    public abstract IComponent getFaxComponent();

    @Component(id = "altFax", type = "TextField", bindings = {"displayName=message:alt-fax-label", "value=prop:alternateFax"})
    public abstract IComponent getAltFaxComponent();

    @Component(id = "role", type = "Checkbox", bindings = {"displayName=message:role-label", "value=prop:role"})
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

    @Component(id = "countries", type = "PropertySelection", bindings = {
            "displayName=literal:Country:", "model=prop:countryCodeModel", "value=prop:country",
            "validators=validators:required,countryCodeValidator"
            })
    public abstract IComponent getStatesComponent();

    @Parameter(required = true)
    public abstract ContactAttributesEditor getEditor();

    @Parameter(required = true)
    public abstract Map<String, String> getContactAttributes();

    @Parameter(required = false, defaultValue = "true")
    public abstract boolean isDisplayFormButtons();

    @Parameter(required = false, defaultValue = "true")
    public abstract boolean isValidationRequired();

    @InjectComponent("name")
    public abstract IFormComponent getNameField();

    @InjectComponent("organisation")
    public abstract IFormComponent getOrganisationField();

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

    @InjectComponent("countries")
    public abstract IFormComponent getCountryField();


    @InjectObject("service:tapestry.globals.HttpServletRequest")
    public abstract HttpServletRequest getHttpRequest();

    @InjectObject("service:rzm.UserServices")
    public abstract UserServices getUserServices();

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

    @Persist("client:form")
    public abstract List<Value> getCountryNames();

    public abstract void setCountryNames(List<Value> countrys);

    public Value getCountry(){
        final String code = getContactAttributes().get(ContactVOWrapper.COUNTRY);

        Value value = ListUtil.find(getCountryNames(), new ListUtil.Predicate<Value>() {
            public boolean evaluate(Value object) {
                return object.getValueId().equals(code);
            }
        });

        if(value == null){
            return NO_COUNTRY;
        }

        return value;
    }

    public void setCountry(Value country){
        getContactAttributes().put(ContactVOWrapper.COUNTRY, country.getValueId());
    }

    public boolean getRole() {
        String role = getContactAttributes().get(ContactVOWrapper.ROLE);
        return Boolean.valueOf(role);
    }

    public void setRole(boolean value) {
        String role = String.valueOf(value);
        getContactAttributes().put(ContactVOWrapper.ROLE, role);
    }

    public CountryCodeModel getCountryCodeModel() {
        List<Value> countries = getCountryNames();
//        Collections.sort(countries, new CountryCodeSorter());
        countries.add(0, NO_COUNTRY);
        return new CountryCodeModel(countries);
    }

    public void pageBeginRender(PageEvent event) {
        setCountryNames(getUserServices().getCountrys());

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

        if(isValidationRequired()){
            validateInput();
            if (getEditor().getValidationDelegate().getHasErrors()) {
                return;
            }
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

    public String getContactName(){
        String s = getContactAttributes().get(ContactVOWrapper.NAME);
        if(s != null){
            s = s.replace("\r", " ");
            s = s.replace("\n", " ");
        }

        return s;
    }

    public void setContactName(String name){
        String oldValue = getContactName();
        if(!StringUtils.equals(oldValue, name)){
            getContactAttributes().put(ContactVOWrapper.NAME, name);    
        }
    }

    public String getOrgName(){
        String s = getContactAttributes().get(ContactVOWrapper.ORGANISATION);
        if(s != null){
            s = s.replace("\r", " ");
            s = s.replace("\n", " ");
        }

        return s;
    }

    public void setOrgName(String name){
        String oldValue = getOrgName();
        if(!StringUtils.equals(oldValue, name)){
            getContactAttributes().put(ContactVOWrapper.ORGANISATION, name);
        }
    }


    protected void validateInput() {
        Map<String, String> contactAttributes = getContactAttributes();
        validateReqiredField(contactAttributes, ContactVOWrapper.NAME, getNameField());
        validateReqiredField(contactAttributes, ContactVOWrapper.ORGANISATION, getOrganisationField());
        validateReqiredField(contactAttributes, ContactVOWrapper.EMAIL, getEmailField());
        validateReqiredField(contactAttributes, ContactVOWrapper.PHONE, getPhoneField());
        validateReqiredField(contactAttributes, ContactVOWrapper.ADDRESS, getAddressField());


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

    protected void validateCountryCode(String code) {
        if (code != null) {
            if (!getUserServices().isValidCountryCode(code)) {
                getEditor().setErrorField(getCountryField(), "Invalid Country Code " + code);
            }
        }
    }

   protected void validateEmail(String email, IFormComponent field, String fieldName) {
        EmailValidator validator = EmailValidator.getInstance();

        if (StringUtils.isBlank(email)) {
            getEditor().setErrorField(field, "Please specify value for " + fieldName);
            return;
        }

        if (!validator.isValid(email)) {
            getEditor().setErrorField(field, "Invalid email address format " + email);
        }
    }

    protected void validateReqiredField(Map attributes, String fieldName, IFormComponent field) {
        String value = (String) attributes.get(fieldName);
        validateField(value, field, fieldName);
    }

    protected void validateField(String value, IFormComponent field, String fieldName) {
        if (StringUtils.isBlank(value)) {
            getEditor().setErrorField(field, "Please specify value for " + fieldName);
        }
    }


    private class CountryCodeModel implements IPropertySelectionModel, Serializable {
        private List<Value> codes;

        public CountryCodeModel(List<Value> codes) {
            this.codes = codes;
        }

        public int getOptionCount() {
            return codes.size();
        }

        public Object getOption(int index) {
            return codes.get(index);
        }

        public String getLabel(int index) {
            return codes.get(index).getValueName();
        }

        public String getValue(int index) {
            return codes.get(index).getValueId();
        }

        public Object translateValue(String value) {
            for (Value code : codes) {
                if (code.getValueId().equals(value)) {
                    return code;
                }
            }

            throw new IllegalArgumentException("Can not find value " + value);
        }
    }
}

