package org.iana.rzm.web.tapestry.components.password;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.valid.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.web.common.pages.*;

public abstract class CreateNewPasswordEditor extends BaseComponent{


    @Component(id = "createPassword", type = "Form", bindings = {
        "delegate=prop:validationDelegate",
        "stateful=literal:false"
        })
    public abstract IComponent getChangePasswordFormComponent();

    @Component(id = "newPassword", type = "TextField", bindings = {
        "value=prop:newPassword",
        "hidden=literal:true",
        "displayName=literal:New Password:",
        "validators=validators:required"
        }
    )
    public abstract IComponent getNewPaswwordComponent();

    @Component(id = "newPasswordLabel", type = "FieldLabel", bindings = {"field= component:newPassword"})
    public abstract IComponent getNewPasswordLabelComponent();

    @Component(id = "confirmPassword", type = "TextField", bindings = {
        "value=prop:confirmNewPassword",
        "hidden=literal:true",
        "displayName=literal:Confirm new  Password:",
        "validators=validators:required"
        }
    )
    public abstract IComponent getConfirmPaswwordComponent();

    @Component(id = "confirmPasswordLabel", type = "FieldLabel", bindings = {"field= component:confirmPassword"})
    public abstract IComponent getConfirmPasswordLabelComponent();

    @Component(id = "changePassword", type = "LinkSubmit")
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:cancel"})
    public abstract IComponent getCancelComponent();

    @InjectComponent("newPassword")
    public abstract IFormComponent getNewPasswordField();

    public abstract PasswordEditor getPasswordEditor();


    @Persist("client")
    public abstract String getUserName();
    public abstract void setUserName(String userName);

    @Persist("client")
    public abstract String getToken();
    public abstract void setToken(String token);

    public abstract String getNewPassword();
    public abstract String getConfirmNewPassword();

    

    public void changePassword() {
        if (getPasswordEditor().hasErrors()) {
            return;
        }

        IValidationDelegate delegate = getPasswordEditor().getValidationDelegate();

        if (!getNewPassword().equals(getConfirmNewPassword())) {
            delegate.setFormComponent(getNewPasswordField());
            delegate.record(getMessage(getPasswordEditor().getMessageUtil().getPasswordMissmatchErrorMessage()), ValidationConstraint.PATTERN_MISMATCH);
       }

        if (getPasswordEditor().hasErrors()) {
            return;
        }

        try {
            getPasswordEditor().getAuthenticationService().newPassword(getNewPassword(), getUserName(), getToken(), getConfirmNewPassword());
        } catch (PasswordChangeException e) {
            delegate.setFormComponent(null);
            delegate.record("Invalid User " + e.getUserName(), ValidationConstraint.CONSISTENCY);
            return;
        }
        BaseLogin login = getPasswordEditor().getLoginPage();
        login.setInfoMessage(getPasswordEditor().getMessageUtil().getPasswordResetOkMessage());
        getPage().getRequestCycle().activate(login);
    }

    public void cancel() {
        getPasswordEditor().getValidationDelegate().clearErrors();
        BaseLogin login = getPasswordEditor().getLoginPage();
        getPage().getRequestCycle().activate(login);
    }

    public IValidationDelegate getValidationDelegate(){
        return getPasswordEditor().getValidationDelegate(); 
    }

}
