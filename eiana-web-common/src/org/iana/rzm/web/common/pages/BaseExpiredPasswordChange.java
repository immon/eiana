package org.iana.rzm.web.common.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.form.IFormComponent;
import org.iana.rzm.facade.passwd.PasswordChangeException;
import org.iana.rzm.web.common.services.ChangePasswordService;


public abstract class BaseExpiredPasswordChange extends RzmPage {

    public static final String PAGE_NAME = "ExpiredPasswordChange";

    @Component(id = "changePasswordForm", type = "Form", bindings = {
            "clientValidationEnabled=literal:true",
            "delegate=prop:validationDelegate",
            "cancel=listener:cancel"
            })
    public abstract IComponent getChangePasswordFormComponent();

    @Component(id = "currentPassword", type = "TextField", bindings = {
            "value=prop:currentPassword",
            "hidden=literal:true",
            "displayName=literal:Current Password",
            "validators=validators:required"
            }
    )
    public abstract IComponent getCurrentPasswordComponent();

    @Component(id = "newPassword", type = "TextField", bindings = {
            "value=prop:newPassword",
            "hidden=literal:true",
            "displayName=literal:New Password",
            "validators=validators:required"
            }
    )
    public abstract IComponent getNewPaswwordComponent();

    @Component(id = "confirmPassword", type = "TextField", bindings = {
            "value=prop:confirmNewPassword",
            "hidden=literal:true",
            "displayName=literal:Confirm New Password",
            "validators=validators:required"
            }
    )
    public abstract IComponent getConfirmPaswwordComponent();

    @Component(id="userName", type="Hidden", bindings = {"value=prop:userName"})
    public abstract IFormComponent getUserNameComponent();

    @Component(id="continueHidden", type="Hidden", bindings = {"value=prop:showContinue"})
    public abstract IFormComponent getContinueHiddenComponent();


    @Component(id = "continue", type = "DirectLink", bindings = {"listener=listener:proceed"})
    public abstract IComponent getContinueComponent();

    @Component(id="showContinue", type="If", bindings = {"condition=prop:continueVisible"})
    public abstract IComponent getShowContinueComponent();


    @InjectPage(BaseLogin.PAGE_NAME)
    public abstract BaseLogin getLogin();

    @InjectObject("service:rzm.ChangePasswordService")
    public abstract ChangePasswordService getChangePasswordService();

    public abstract String getUserName();
    public abstract void setUserName(String userName);

    public abstract void setShowContinue(boolean value);
    public abstract boolean isShowContinue();

    public abstract String getNewPassword();
    public abstract String getCurrentPassword();
    public abstract String getConfirmNewPassword();



    public boolean isContinueVisible(){
        boolean result =  isShowContinue();
        return result;
    }

    public void cancel() {
        getValidationDelegate().clearErrors();
        getLogin().activate();
    }

    public void proceed() {
        getLogin().activate();
    }

    public void changePassword() {
        validateSave();

        if (getValidationDelegate().getHasErrors()) {
            return;
        }
        changePassword(getCurrentPassword(), getNewPassword(), getConfirmNewPassword());
    }


    private void validateSave() {
        if (getNewPassword().equals(getCurrentPassword())) {
            setErrorField((IFormComponent) getComponent("newPassword"), getMessageUtil().getPasswordTheSameMessage());
        } else if (!getNewPassword().equals(getConfirmNewPassword())) {
            setErrorField((IFormComponent) getComponent("newPassword"), getMessageUtil().getPasswordMismatchMessage());
        }
    }

    private void changePassword(String currentPassword, String newPassword, String confirmNewPassword) {
        try {
            getChangePasswordService().changePassword(getUserName(), currentPassword, newPassword, confirmNewPassword);
            setInfoMessage(getMessageUtil().passwordChangeSuccessfull());
            setShowContinue(true);
        } catch (PasswordChangeException e) {
            setErrorMessage(e.getMessage());
        }

    }

}
