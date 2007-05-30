package org.iana.rzm.web.pages;

import org.apache.log4j.Logger;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.services.CookieSource;
import org.iana.rzm.facade.auth.AuthenticationFailedException;
import org.iana.rzm.facade.auth.AuthenticationRequiredException;
import org.iana.rzm.web.Visit;
import org.iana.rzm.web.model.WebUser;
import org.iana.rzm.web.services.ApplicationLifecycle;
import org.iana.rzm.web.services.LoginController;
import org.iana.rzm.web.services.RzmAuthenticationService;
import org.iana.rzm.web.tapestry.RzmCallback;

public abstract class SecureId extends RzmPage implements IExternalPage {

    private static final Logger logger = Logger.getLogger(SecureId.class.getName());

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

    @InjectPage("Login")
    public abstract Login getLoginPage();

    @Persist("client:page")
    public abstract RzmCallback getCallback();

    @Persist("client:page")
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
            getCookieSource().writeCookieValue(Login.COOKIE_NAME, getUserName(), Login.COOKIE_MAX_AGE);
            cycle.forgetPage(getPageName());
            return iLink;
        } catch (AuthenticationFailedException e) {
            log(logger, "SecureId AuthenticationFailed Exception", e);
            setErrorMessage(e.getMessage());
            return null;
        } catch (AuthenticationRequiredException e) {
            log(logger, "SecureId AuthenticationRequired Exception Exception", e);
            Login login = getLoginPage();
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
