package org.iana.rzm.web.common.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.engine.*;
import org.apache.tapestry.services.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.auth.securid.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.callback.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.services.*;
import org.iana.web.tapestry.session.*;

public abstract class BaseSecureId extends RzmPage implements IExternalPage {


    public static final String PAGE_NAME = "SecureId";

    @Component(id = "code", type = "TextField", bindings = {
        "displayName=literal:Code:",
        "value=prop:code",
        "validators=validators:required"
        })
    public abstract IComponent getCodeComponent();

    @Component(id = "form", type = "Form", bindings = {
        "success=listener:checkToken",
        "stateful=literal:false",
        "clientValidationEnabled=literal:true",
        "cancel=listener:logout"
        })
    public abstract IComponent getFormComponent();

    @Component(id = "cancel", type = "Submit", bindings = {"listener=listener:logout"})
    public abstract IComponent getCancelComponent();

    @InjectObject("service:rzm.ApplicationLifecycle")
    public abstract ApplicationLifecycle getApplicationLifecycle();

    @InjectObject("infrastructure:cookieSource")
    public abstract CookieSource getCookieSource();

    @InjectObject("service:rzm.LoginController")
    public abstract LoginController getLoginController();

    @InjectObject("service:rzm.RzmAuthenticationService")
    public abstract RzmAuthenticationService getAuthenticationService();

    @InjectPage(BaseLogin.PAGE_NAME)
    public abstract BaseLogin getLoginPage();

    @InjectPage(BaseSecureIdNextCode.PAGE_NAME)
    public abstract BaseSecureIdNextCode getSecureIdNextCodePage();

    @InjectPage(BaseSecureIdNewPin.PAGE_NAME)
    public abstract BaseSecureIdNewPin getSecureIdNewPinPage();

    @InjectObject("engine-service:home")
    public abstract IEngineService getHomeService();

    @InjectObject("engine-service:page")
    public abstract IEngineService getPageService();

    @InjectObject("engine-service:external")
    public abstract IEngineService getExternalService();

    @InjectState("visit")
    public abstract Visit getVisitState();

    @Persist("client")
    public abstract RzmCallback getCallback();
    public abstract void setCallback(RzmCallback callback);

    @Persist("client")
    public abstract void setUserName(String userName);
    public abstract String getUserName();

    @Persist("client")
    public abstract AuthenticationToken getAuthenticationToken();
    public abstract void setAuthenticationToken(AuthenticationToken authenticationToken);


    public abstract void setCode(String code);
    public abstract String getCode();

    protected abstract String getCookieName();

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        String userName = parameters[0].toString();
        AuthenticationToken authenticationToken = (AuthenticationToken) parameters[1];
        setUserName(userName);
        setAuthenticationToken(authenticationToken);
    }

    public ILink checkToken() {

        try {
            WebUser webUser = getAuthenticationService().secureId(getAuthenticationToken(), getUserName(), getCode());
            ILink iLink = getLoginController().loginUser(getService(), getRequestCycle(), getCallback());
            getCookieSource().writeCookieValue(getCookieName(), getUserName(), BaseLogin.COOKIE_MAX_AGE);
            getVisitState().setUser(webUser);
            getRequestCycle().forgetPage(getPageName());
            return iLink;
        } catch (SecurIDNextCodeRequiredException e) {
            BaseSecureIdNextCode page = getSecureIdNextCodePage();
            page.setSessionId(e.getSessionId());
            page.setAuthenticationToken(getAuthenticationToken());
            page.setCallback(getCallback());
            page.setUserName(getUserName());
            throw new PageRedirectException(page);
        } catch (SecurIDNewPinRequiredException e) {
            BaseSecureIdNewPin page = getSecureIdNewPinPage();
            page.setCallback(getCallback());
            page.setAuthenticationToken(getAuthenticationToken());
            page.setSessionId(e.getSessionId());
            page.setUserName(getUserName());
            throw new PageRedirectException(page);
        } catch (AuthenticationRequiredException e) {
            BaseLogin login = getLoginPage();
            login.setSecureIdErrorMessage(e.getRequired().name() + " Authentication required");
            throw new PageRedirectException(login);
        } catch (AuthenticationFailedException e) {
            setErrorMessage("SecureID Authentication Failed Please try again");
        }
        return null;
    }

    public IPage logout() {
        getApplicationLifecycle().logout();
        return getLoginPage();
    }

    private IEngineService getService() {
        if (getCallback() == null) {
            return getHomeService();
        }
        return getPageService();
    }
}
