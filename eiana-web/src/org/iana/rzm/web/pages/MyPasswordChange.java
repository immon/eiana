package org.iana.rzm.web.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.Persist;
import org.iana.rzm.web.tapestry.MessagePropertyCallback;

public abstract class MyPasswordChange extends Protected {

    @Component(id = "changePasswordForm", type = "Form", bindings = {
            "clientValidationEnabled=literal:true", "delegate=prop:validationDelegate", "success=listener:changePassword"})
    public abstract IComponent getChangePasswordFormComponent();

    @Component(id = "currentPassword", type = "TextField", bindings = {
            "value=prop:currentPassword",
            "hidden=literal:true",
            "displayName=message:current-password",
            "validators=validators:required"
            }
    )
    public abstract IComponent getCurrentPasswordComponent();

    @Component(id = "currentPasswordLabel", type = "FieldLabel", bindings = {"field=component:currentPassword"})
    public abstract IComponent getCurrentPasswordLabelComponent();

    @Component(id = "newPassword", type = "TextField", bindings = {
            "value=prop:newPassword",
            "hidden=literal:true",
            "displayName=message:new-password",
            "validators=validators:required"
            }
    )
    public abstract IComponent getNewPaswwordComponent();

    @Component(id = "newPasswordLabel", type = "FieldLabel", bindings = {"field= component:newPassword"})
    public abstract IComponent getNewPasswordLabelComponent();

    @Component(id = "confirmPassword", type = "TextField", bindings = {
            "value=prop:confirmNewPassword",
            "hidden=literal:true",
            "displayName=message:confirm-password",
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

    @Persist("client")
    public abstract void setCallback(MessagePropertyCallback callback);

    public abstract MessagePropertyCallback getCallback();

    public abstract String getCurrentPassword();

    public abstract String getNewPassword();

    public abstract String getConfirmNewPassword();


    public void changePassword() {
        preventResubmission();
        if (hasErrors()) {
            return;
        }

        validateSave();
        if (hasErrors()) {
            return;
        }

        getRzmServices().changePassword(getVisitState().getUserId(),  getNewPassword());
        MessagePropertyCallback calback = getCallback();
        calback.setInfoMessage("Password change successfully");
        calback.performCallback(getRequestCycle());
    }

    public void cancel() {
        getValidationDelegate().clearErrors();
        getCallback().performCallback(getRequestCycle());
    }

    private void validateSave() {
        if (getNewPassword().equals(getCurrentPassword())) {
            setErrorField("newPassword", getMessages().getMessage("user-new-password-same-as-current"));
        } else if (!getNewPassword().equals(getConfirmNewPassword())) {
            setErrorField("newPassword", getMessages().getMessage("user-confirm-new-password-does-not-match"));
        }
    }



}
