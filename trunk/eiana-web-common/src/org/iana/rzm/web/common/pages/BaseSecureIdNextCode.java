package org.iana.rzm.web.common.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.services.*;
import org.iana.rzm.facade.auth.securid.*;
import org.iana.rzm.web.common.callback.*;
import org.iana.rzm.web.common.services.*;
import org.iana.web.tapestry.session.*;

public abstract class BaseSecureIdNextCode extends RzmPage {

    public static final String PAGE_NAME = "SecureIdNextCode";

    @Component(id = "token", type = "TextField", bindings = {
            "displayName=literal:Next Code:",
            "value=prop:nextCode",
            "validators=validators:required"
            })
    public abstract IComponent getCodeComponent();

    @Component(id = "form", type = "Form", bindings = {
            "success=listener:checkNextCode",
            "stateful=literal:false",
            "clientValidationEnabled=literal:true",
            "cancel=listener:logout"
            })
    public abstract IComponent getFormComponent();

    @Component(id = "cancel", type = "Submit", bindings = {"listener=listener:logout"})
    public abstract IComponent getCancelComponent();

    @InjectObject("service:rzm.RzmAuthenticationService")
    public abstract RzmAuthenticationService getAuthenticationService();

    @InjectObject("service:rzm.ApplicationLifecycle")
    public abstract ApplicationLifecycle getApplicationLifecycle();

    @InjectObject("infrastructure:cookieSource")
    public abstract CookieSource getCookieSource();

    @InjectObject("service:rzm.LoginController")
    public abstract LoginController getLoginController();

    @InjectPage("Login")
    public abstract BaseLogin getLoginPage();

    @Persist("client")
    public abstract RzmCallback getCallback();
    public abstract void setCallback(RzmCallback callback);

    @Persist("client")
    public abstract String getSessionId();
    public abstract void setSessionId(String sessionId);

    public abstract String getNextCode();
    public abstract void setNextCode(String code);


    public void checkNextCode(){
        try {
            getAuthenticationService().nextCode(getSessionId(), getNextCode());
        } catch (SecurIDException e) {
            BaseLogin login = getLoginPage();
            login.setSecureIdErrorMessage(e.getMessage());
            throw new PageRedirectException(login);
        }
    }
}
