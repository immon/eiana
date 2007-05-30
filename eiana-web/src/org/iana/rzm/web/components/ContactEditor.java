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

    @Component(id = "phone", type = "TextField", bindings = {
            "displayName=message:phone-label", "value=ognl:contactAttributes.PHONE", "validators=validators:required"})
    public abstract IComponent getPhoneComponent();

    @Component(id = "fax", type = "TextField", bindings = {
            "displayName=message:fax-label", "value=ognl:contactAttributes.FAX"})
    public abstract IComponent getFaxComponent();

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

    public abstract boolean getRole();
    public abstract void setRole(boolean role);

    public void pageBeginRender(PageEvent event){
        String role = getContactAttributes().get(ContactVOWrapper.ROLE);
        if(!event.getRequestCycle().isRewinding()){
            setRole(Boolean.valueOf(role));
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


    private void validateInput() {
        Map<String, String> contactAttributes = getContactAttributes();
        validateReqiredField(contactAttributes, ContactVOWrapper.NAME, getNameField());
        validateReqiredField(contactAttributes, ContactVOWrapper.ORGANISATION, getOrganisationField());
        validateReqiredField(contactAttributes, ContactVOWrapper.EMAIL, getEmailField());
        validateReqiredField(contactAttributes, ContactVOWrapper.PHONE, getPhoneField());
        validateReqiredField(contactAttributes, ContactVOWrapper.ADDRESS, getAddressField());
        validateReqiredField(contactAttributes, ContactVOWrapper.COUNTRY, getCountryField());
    }

    private void validateReqiredField(Map attributes, String fieldName, IFormComponent field) {

        String value = (String) attributes.get(fieldName);
        if (StringUtils.isBlank(value)) {
            getEditor().setErrorField(field, "Please specify value for " + fieldName);
        }
    }

}
