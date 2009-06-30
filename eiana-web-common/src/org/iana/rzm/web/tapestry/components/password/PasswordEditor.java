package org.iana.rzm.web.tapestry.components.password;

import org.apache.tapestry.valid.IValidationDelegate;
import org.iana.rzm.web.common.MessageUtil;
import org.iana.rzm.web.common.pages.BaseLogin;
import org.iana.rzm.web.common.services.RzmAuthenticationService;

public interface PasswordEditor {

    public  RzmAuthenticationService getAuthenticationService();
    public  BaseLogin getLoginPage();
    public  MessageUtil getMessageUtil();
    public IValidationDelegate getValidationDelegate();
    public boolean hasErrors();
}
