package org.iana.rzm.web.admin.pages;

import org.iana.rzm.web.common.pages.*;


public abstract class ResetPassword extends BaseResetPassword {

    public static final String PAGE_NAME = "ResetPassword";

    protected String getCreateNewPasswordPageName() {
        return CreateNewPassword.PAGE_NAME;
    }
}
