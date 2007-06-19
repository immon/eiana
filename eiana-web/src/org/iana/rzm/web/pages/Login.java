package org.iana.rzm.web.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.engine.ExternalServiceParameter;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.services.CookieSource;
import org.apache.tapestry.valid.IValidationDelegate;
import org.iana.rzm.facade.auth.Authentication;
import org.iana.rzm.facade.auth.AuthenticationException;
import org.iana.rzm.facade.auth.AuthenticationRequiredException;
import org.iana.rzm.web.Visit;
import org.iana.rzm.web.model.WebUser;
import org.iana.rzm.web.services.LoginController;
import org.iana.rzm.web.services.RzmAuthenticationService;
import org.iana.rzm.web.tapestry.RzmCallback;

public abstract class Login extends RzmPage implements PageBeginRenderListener  {
    /*
     * The name of a cookie to store on the user's machine that will identify them next time they
     * log in.
     */
    public static final String COOKIE_NAME = "org.iana.rzm.Login.username";

    public static final int COOKIE_MAX_AGE = 60 * 60 * 24 * 7;
    public static final String PAGE_NAME = "Login";


    @Component(id = "form", type = "Form",
            bindings = {
                    "success=listener:attemptLogin",
                    "stateful=literal:false",
                    "clientValidationEnabled=literal:true"
                    }
    )
    public abstract IComponent getFormComponent();

    @Component(id = "username", type = "TextField",
            bindings = {
                    "displayName=message:user-label",
                    "value=prop:userName",
                    "validators=validators:required"
                    }
    )
    public abstract IComponent getUserNameComponent();

    @Component(id = "password", type = "TextField",
            bindings = {
                    "displayName=message:password-label",
                    "value=prop:password",
                    "validators=validators:required",
                    "hidden=literal:true"
                    }
    )
    public abstract IComponent getPasswordComponent();

    @InjectObject("engine-service:external")
    public abstract IEngineService getExternalPageService();

    @InjectObject("engine-service:home")
    public abstract IEngineService getHomeService();

    @InjectObject("engine-service:page")
    public abstract IEngineService getPageService();

    @InjectObject("infrastructure:cookieSource")
    public abstract CookieSource getCookieSource();

    @InjectObject("service:rzm.RzmAuthenticationService")
    public abstract RzmAuthenticationService getAuthenticationService();

    @InjectObject("service:rzm.LoginController")
    public abstract LoginController getLoginController();

    @Persist("client:page")
    public abstract RzmCallback getCallback();

    public abstract void setCallback(RzmCallback callback);

    @InjectComponent("username")
    public abstract IFormComponent getUserNameField();

    @InjectComponent("password")
    public abstract IFormComponent getPasswordField();

    @InjectPage("SecureId")
    public abstract SecureId getSecureIdPage();

    @Persist("client:page")
    public abstract void setSessionTimeOutMessage(String message);
    public abstract String getSessionTimeOutMessage();

    public abstract void setUserName(String value);

    public abstract String getUserName();

    public abstract String getPassword();

    public abstract void setPassword(String password);

    public void pageBeginRender(PageEvent event) {
        if (getUserName() == null){
            setUserName(getCookieSource().readCookieValue(COOKIE_NAME));
        }

        setErrorMessage(getSessionTimeOutMessage());
        setWarningMessage("Please note: This is a test environment to test the new automation system at IANA. Currently NameServer changes are not supported");
        setSessionTimeOutMessage(null);
    }

 
    /**
     * Attempts to login.
     * <p/>
     * If the user name is not known, or the password is invalid, then an error message is
     * displayed.
     */

    public ILink attemptLogin(IRequestCycle cycle) {
        final String password = getPassword();
        // Do a little extra work to clear out the password.

        setPassword(null);
        IValidationDelegate delegate = getValidationDelegate();
        delegate.setFormComponent(getPasswordField());
        delegate.recordFieldInputValue(null);

        // An error, from a validation field, may already have occured.

        if (delegate.getHasErrors())
            return null;


        try {
            WebUser user = getAuthenticationService().login(getUserName(), password);
            return loginUser(user);
        }
        catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.SECURID) {
                return redirectToSecureIdPage();
            }
            IValidationDelegate validationDelegate = getValidationDelegate();
            validationDelegate.record(e.getMessage(), null);
        }
        catch (AuthenticationException ex) {
            IFormComponent field = getUserNameField();
            IValidationDelegate validationDelegate = getValidationDelegate();
            validationDelegate.setFormComponent(field);
            validationDelegate.record(ex.getMessage(), null);
        }
        return null;

    }

    private ILink redirectToSecureIdPage() {
        SecureId secureIdPage = getSecureIdPage();
        ExternalServiceParameter parameter =
                new ExternalServiceParameter(secureIdPage.getPageName(), new Object[]{getUserName()});
        return getExternalPageService().getLink(true, parameter);
    }


    /**
     * Sets up the {@link org.iana.rzm.facade.auth.AuthenticatedUser} as the logged in user, creates a cookie for thier user name
     * (for subsequent logins), and redirects to the appropriate page, or a
     * specified page).
     *
     * @param user
     * @return
     */

    private ILink loginUser(WebUser user) {
        IRequestCycle cycle = getRequestCycle();
        Visit visit = getVisitState();
        visit.setUser(user);
        IEngineService engineService = getService();
        ILink iLink = getLoginController().loginUser(engineService, cycle, getCallback());
        getCookieSource().writeCookieValue(Login.COOKIE_NAME, getUserName(), Login.COOKIE_MAX_AGE);
        cycle.forgetPage(getPageName());
        return iLink;
    }

    private IEngineService getService() {
        if (getCallback() == null) {
            return getHomeService();
        }

        boolean external = getCallback().isExternal();

        if(external){
            return getExternalPageService();
        }

        return getPageService();
    }


}

