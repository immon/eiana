package org.iana.rzm.web.tapestry.components.password;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.form.IFormComponent;
import org.iana.rzm.web.common.listeners.MyChangePasswordListener;

public abstract class MyPasswordChangeEditor extends BaseComponent {

    @Component(id = "changePasswordForm", type = "Form", bindings = {
        "clientValidationEnabled=literal:true",
        "delegate=prop:listener.validationDelegate",
        "success=listener:changePassword"
        })
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

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:cancel",  "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    public abstract MyChangePasswordListener getListener();

    public abstract String getCurrentPassword();
    public abstract String getNewPassword();

    public abstract String getConfirmNewPassword();


    public void changePassword() {
        validateSave();

        if(getListener().getValidationDelegate().getHasErrors()){
            return;
        }
        getListener().changePassword(getCurrentPassword(),getNewPassword(), getConfirmNewPassword());
    }

    public void cancel() {
        getListener().getValidationDelegate().clearErrors();
        getListener().cancel();
    }

    private void validateSave() {
        if (getNewPassword().equals(getCurrentPassword())) {
            getListener().reportError((IFormComponent)getComponent("newPassword"), getMessage("user-new-password-same-as-current"));
        } else if (!getNewPassword().equals(getConfirmNewPassword())) {
            getListener().reportError((IFormComponent)getComponent("newPassword"), getMessage("user-confirm-new-password-does-not-match"));
        }
    }
}
