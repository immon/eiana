package org.iana.rzm.web.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.valid.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.web.services.*;

public abstract class CreateNewPassword extends RzmPage implements IExternalPage{

    public static final String PAGE_NAME = "CreateNewPassword";

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

    @InjectObject("service:rzm.UserExternalPageErrorHandler")
    public abstract ExternalPageErrorHandler getExternalPageErrorHandler();

    @InjectComponent("newPassword")
    public abstract IFormComponent getNewPasswordField();

    @InjectPage("Login")
    public abstract Login getLoginPage();

    @InjectObject("service:rzm.RzmAuthenticationService")
    public abstract RzmAuthenticationService getAuthenticationService();

    @Persist("client:form")
    public abstract String getUserName();
    public abstract void setUserName(String userName);

    @Persist("client:form")
    public abstract String getToken();
    public abstract void setToken(String token);

    public abstract String getNewPassword();
    public abstract String getConfirmNewPassword();

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){
        if(parameters == null || parameters.length < 2){
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        String userName = (String) parameters[0];
        setUserName(userName);
        setToken(parameters[1].toString());
    }

    public void changePassword() {
        if (hasErrors()) {
            return;
        }

        IValidationDelegate delegate = getValidationDelegate();

        if (!getNewPassword().equals(getConfirmNewPassword())) {
            delegate.setFormComponent(getNewPasswordField());
            delegate.record(getMessage("user-confirm-new-password-does-not-match"), ValidationConstraint.PATTERN_MISMATCH);
       }

        if (hasErrors()) {
            return;
        }

        try {
            getAuthenticationService().newPassword(getNewPassword(), getUserName(), getToken(), getConfirmNewPassword());
        } catch (PasswordChangeException e) {
            delegate.setFormComponent(null);
            delegate.record("Invalid User " + e.getUserName(), ValidationConstraint.CONSISTENCY);
            return;
        }
        Login login = getLoginPage();
        login.setInfoMessage("Your Password was reset Successfully");
        getRequestCycle().activate(login);
    }

    public void cancel() {
        getValidationDelegate().clearErrors();
        Login login = getLoginPage();
        getRequestCycle().activate(login);
    }

}
