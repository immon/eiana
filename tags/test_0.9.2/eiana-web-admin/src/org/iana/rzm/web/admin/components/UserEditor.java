package org.iana.rzm.web.admin.components;

import org.apache.commons.lang.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.valid.*;
import org.iana.rzm.web.admin.pages.editors.*;
import org.iana.rzm.web.common.model.*;


public abstract class UserEditor extends BaseComponent implements PageBeginRenderListener {

    @Component(id = "userEditor", type = "Form", bindings = {
        "clientValidationEnabled=literal:true",
        "success=listener:save",
        "delegate=prop:validationDelegate"
        })
    public abstract IComponent getEditContactComponent();

    @Component(id = "username", type = "TextField",
               bindings = {"value=prop:userName", "displayName=message:username-label",
                   "validators=validators:required"})
    public abstract IComponent getUserNameComponent();

    @Component(id = "password", type = "TextField",
               bindings = {"displayName=message:password-label", "value=prop:password", "validators=validators:required",
                   "hidden=literal:true", "disabled=prop:passwordDisabled"})
    public abstract IComponent getPasswordComponent();

    @Component(id = "confirmedPassword", type = "TextField",
               bindings = {"displayName=message:confirm-password-label", "value=prop:confirmPassword", "validators=validators:required",
                   "hidden=literal:true", "disabled=prop:passwordDisabled"})
    public abstract IComponent getConfirmedPasswordComponent();

    @Component(id = "name", type = "TextField", bindings = {"value=prop:name", "displayName=message:name-label",
        "validators=validators:required"})
    public abstract IComponent getNameComponent();

    @Component(id = "lastname",
               type = "TextField",
               bindings = {"value=prop:lastName", "displayName=message:lastname-label"})
    public abstract IComponent getLastNameComponent();

    @Component(id = "organization",
               type = "TextField",
               bindings = {"value=prop:organization", "displayName=message:organization-label"})
    public abstract IComponent getOrganizationComponent();

    @Component(id = "email", type = "TextField", bindings = {"value=prop:email", "displayName=message:email-label",
        "validators=validators:required, email"})
    public abstract IComponent getEmailComponent();

    @Component(id = "pgp", type = "TextArea", bindings = {"displayName=message:pgp-label", "value=prop:publicKey"})
    public abstract IComponent getPublicKeyComponent();

    @Component(id = "secureid", type = "Checkbox", bindings = {
        "displayName=message:secureid-label", "value=prop:useSecureId"})
    public abstract IComponent getSecureidComponent();

    @Component(id = "secureidLabel", type = "FieldLabel", bindings = {"field=component:secureid"})
    public abstract IComponent getRoleLabelComponent();

    @Component(id = "save", type = "LinkSubmit")
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @Asset("images/spacer.png")
    public abstract IAsset getSpacer();

    @InjectComponent("password")
    public abstract IFormComponent getPasswordField();

    @Parameter(required = true)
    public abstract boolean isCreate();

    @Parameter(required = true)
    public abstract UserAttributeEditor getListener();

    @Parameter(required = true)
    public abstract UserVOWrapper getUser();
    public abstract void setUser(UserVOWrapper user);

    public abstract void setUserName(String userName);

    public abstract String getUserName();

    public abstract void setPassword(String password);

    public abstract String getPassword();

    public abstract void setConfirmPassword(String password);

    public abstract String getConfirmPassword();

    public abstract void setName(String name);

    public abstract String getName();

    public abstract void setLastName(String name);

    public abstract String getLastName();

    public abstract void setOrganization(String org);

    public abstract String getOrganization();

    public abstract void setEmail(String email);

    public abstract String getEmail();

    public abstract void setPublicKey(String key);

    public abstract String getPublicKey();

    public abstract void setUseSecureId(boolean value);

    public abstract boolean isUseSecureId();

    public void pageBeginRender(PageEvent event) {

        UserVOWrapper user = getUser();

        if (!event.getRequestCycle().isRewinding()) {

            if (getUserName() == null) {
                setUserName(user.getUserName());
            }

            if (getName() == null) {
                setName(user.getFirstName());
            }

            if (getLastName() == null) {
                setLastName(user.getLastName());
            }

            if (getOrganization() == null) {
                setOrganization(user.getOrganization());
            }
            if (getEmail() == null) {
                setEmail(user.getEmail());
            }

            if (getPublicKey() == null) {
                setPublicKey(user.getPublickey());
            }

            setUseSecureId(user.isUseSecureId());

        }


    }

    public boolean isPasswordDisabled() {
        return !isCreate();
    }

    public IValidationDelegate getValidationDelegate() {
        return getListener().getValidationDelegate();
    }

    public void revert() {
        getListener().revert();
    }


    protected void validateInput() {
        if (isCreate()) {
            String password = getPassword();
            String confirmPassword = getConfirmPassword();
            if (!StringUtils.equals(confirmPassword, password)) {
                getListener().setErrorField(getPasswordField(), "Paswords are not the same");
            }
        }

    }
}