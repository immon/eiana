package org.iana.rzm.web.common.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.auth.securid.*;
import org.iana.rzm.web.common.callback.*;
import org.iana.rzm.web.common.services.*;
import org.iana.web.tapestry.session.*;

public abstract class BaseSecureIdNewPin extends RzmPage {
    public static final String PAGE_NAME = "SecureIdNewPin";

    @Component(id = "pin", type = "TextField", bindings = {
        "displayName=literal:Pin:",
        "value=prop:pin",
        "validators=validators:required"
        })
    public abstract IComponent getPinComponent();

    @Component(id = "confirmPin", type = "TextField", bindings = {
        "displayName=literal:Confirm Pin:",
        "value=prop:confirmPin",
        "validators=validators:required"
        })
    public abstract IComponent getConfirmPinComponent();


    @Component(id = "form", type = "Form", bindings = {
        "success=listener:newPin",
        "stateful=literal:false",
        "clientValidationEnabled=literal:true",
        "cancel=listener:logout"
        })
    public abstract IComponent getFormComponent();

    @Component(id = "cancel", type = "Submit", bindings = {"listener=listener:logout"})
    public abstract IComponent getCancelComponent();

    @InjectObject("service:rzm.ApplicationLifecycle")
    public abstract ApplicationLifecycle getApplicationLifecycle();

    @InjectObject("service:rzm.RzmAuthenticationService")
    public abstract RzmAuthenticationService getAuthenticationService();

    @InjectPage(BaseLogin.PAGE_NAME)
    public abstract BaseLogin getLoginPage();

    @InjectPage(BaseSecureId.PAGE_NAME)
    public abstract BaseSecureId getSecureIdPage();


    @Persist("client")
    public abstract String getSessionId();
    public abstract void setSessionId(String sessionId);

    @Persist("client")
    public abstract AuthenticationToken getAuthenticationToken();
    public abstract void setAuthenticationToken(AuthenticationToken token);

    @Persist("client")
    public abstract void setCallback(RzmCallback callback);
    public abstract RzmCallback getCallback();

    @Persist("client")
    public abstract void setUserName(String userName);
    public abstract String getUserName();

    public abstract String getPin();
    public abstract String getConfirmPin();

    public void newPin() {
        try {
            getAuthenticationService().newPin(getSessionId(), getPin());
            BaseSecureId page = getSecureIdPage();
            page.setAuthenticationToken(getAuthenticationToken());
            page.setUserName(getUserName());
            page.setCallback(getCallback());
            page.activate();
        } catch (SecurIDInvalidPinException e) {
            setErrorMessage(e.getMessage());
        } catch (SecurIDException e) {
            BaseLogin login = getLoginPage();
            login.setSecureIdErrorMessage(e.getMessage());
            throw new PageRedirectException(login);
        }
    }

    public IPage logout() {
        getApplicationLifecycle().logout();
        return getLoginPage();
    }

}
