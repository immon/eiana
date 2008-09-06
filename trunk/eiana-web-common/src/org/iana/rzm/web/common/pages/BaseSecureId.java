package org.iana.rzm.web.common.pages;

import org.apache.log4j.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.engine.*;
import org.apache.tapestry.services.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.callback.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.services.*;
import org.iana.web.tapestry.session.*;

public abstract class BaseSecureId extends RzmPage implements IExternalPage {

    private static final Logger logger = Logger.getLogger(BaseSecureId.class.getName());
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

    @InjectPage("BaseLogin")
    public abstract BaseLogin getLoginPage();

    @Persist("client")
    public abstract RzmCallback getCallback();

    @Persist("client")
    public abstract void setUserName(String userName);

    public abstract String getUserName();

    @InjectObject("engine-service:home")
    public abstract IEngineService getHomeService();

    @InjectObject("engine-service:page")
    public abstract IEngineService getPageService();

    @InjectObject("engine-service:external")
    public abstract IEngineService getExternalService();

    @InjectObject("service:rzm.RzmAuthenticationService")
    public abstract RzmAuthenticationService getSecureIdService();

     @InjectState("visit")
    public abstract Visit getVisitState();

    public abstract void setCode(String code);

    public abstract String getCode();

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        String userName = (String) parameters[0];
        setUserName(userName);
    }

    public ILink checkToken() {

        IRequestCycle cycle = getRequestCycle();
        Visit visit = getVisitState();
        IEngineService engineService = getService();

        try {
            WebUser webUser = getSecureIdService().secureId(getUserName(), getCode());
            ILink iLink = getLoginController().loginUser(engineService, cycle, getCallback());
            visit.setUser(webUser);
            getCookieSource().writeCookieValue(BaseLogin.COOKIE_NAME, getUserName(), BaseLogin.COOKIE_MAX_AGE);
            cycle.forgetPage(getPageName());
            return iLink;
        } catch (AuthenticationFailedException e) {
            log(logger, "BaseSecureId AuthenticationFailed Exception", e);
            setErrorMessage(e.getMessage());
            return null;
        } catch (AuthenticationRequiredException e) {
            log(logger, "BaseSecureId AuthenticationRequired Exception Exception", e);
            BaseLogin login = getLoginPage();
            login.setErrorMessage(e.getMessage());
            throw new PageRedirectException(login);
        }
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
