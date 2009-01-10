package org.iana.rzm.web.common.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.engine.*;
import org.apache.tapestry.services.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.auth.securid.*;
import org.iana.rzm.web.common.callback.*;
import org.iana.rzm.web.common.services.*;
import org.iana.web.tapestry.session.*;

public abstract class BaseSecureIdNextCode extends RzmPage {

    public static final String PAGE_NAME = "SecureIdNextCode";

    @Component(id = "code", type = "TextField", bindings = {
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

    @InjectObject("engine-service:home")
    public abstract IEngineService getHomeService();

    @InjectObject("engine-service:page")
    public abstract IEngineService getPageService();

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

    @Persist("client")
    public abstract String getUserName();
    public abstract void setUserName(String userName);

    @Persist("client")
    public abstract AuthenticationToken getAuthenticationToken();
    public abstract void setAuthenticationToken(AuthenticationToken token);

    public abstract String getNextCode();
    public abstract void setNextCode(String code);


    public ILink checkNextCode() {
        try {
            getAuthenticationService().nextCode(getSessionId(), getNextCode());
            //WebUser webUser = getAuthenticationService().nextCode(getSessionId(), getNextCode());
            ILink iLink = getLoginController().loginUser(getService(), getRequestCycle(), getCallback());
            getCookieSource().writeCookieValue(getCookieName(), getUserName(), BaseLogin.COOKIE_MAX_AGE);
            //getVisitState().setUser(webUser);
            getRequestCycle().forgetPage(getPageName());
            return iLink;

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

    protected abstract String getCookieName();

    private IEngineService getService() {
        if (getCallback() == null) {
            return getHomeService();
        }
        return getPageService();
    }
}
