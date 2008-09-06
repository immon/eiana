package org.iana.rzm.web.tapestry.components.password;

import org.apache.tapestry.valid.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.pages.*;
import org.iana.rzm.web.common.services.*;

public interface PasswordEditor {

    public  RzmAuthenticationService getAuthenticationService();
    public  BaseLogin getLoginPage();
    public  MessageUtil getMessageUtil();
    public IValidationDelegate getValidationDelegate();
    public boolean hasErrors();
}
