package org.iana.rzm.web.common.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IPage;
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
import org.iana.commons.StringUtil;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.web.common.Visit;
import org.iana.rzm.web.common.callback.RzmCallback;
import org.iana.rzm.web.common.model.WebUser;
import org.iana.rzm.web.common.services.LoginController;
import org.iana.rzm.web.common.services.RzmAuthenticationService;

public abstract class BaseLogin extends RzmPage implements PageBeginRenderListener {

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

    @Component(id = "resetPassword",
            type = "PageLink",
            bindings = {"page=literal:ResetPassword", "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getResetPasswordComponent();

    @Component(id = "recoverUserName",
            type = "PageLink",
            bindings = {"page=literal:RecoverUserName", "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getRecoverUserNamedComponent();

    @Component(id = "rememberMe", type = "Checkbox", bindings = {"value=prop:rememberMe"})
    public abstract IComponent getAltFaxCheckBoxComponent();

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

    @InjectComponent("username")
    public abstract IFormComponent getUserNameField();

    @InjectComponent("password")
    public abstract IFormComponent getPasswordField();

    @InjectPage(BaseSecureId.PAGE_NAME)
    public abstract IPage getSecureIdPage();

    @InjectPage(BaseExpiredPasswordChange.PAGE_NAME)
    public abstract IPage getExpiredPasswordChange();

    @InjectState("visit")
    public abstract Visit getVisitState();

    @InjectStateFlag("visit")
    public abstract boolean getVisitStateExists();

    @Persist()
    public abstract RzmCallback getCallback();
    public abstract void setCallback(RzmCallback callback);

    @Persist("client")
    public abstract void setSessionTimeOutMessage(String message);
    public abstract String getSessionTimeOutMessage();

    @Persist("client")
    public abstract void setSecureIdErrorMessage(String message);
    public abstract String getSecureIdErrorMessage();


    public abstract void setUserName(String value);
    public abstract String getUserName();

    public abstract boolean isRememberMe();
    public abstract String getPassword();

    public abstract void setPassword(String password);
    protected abstract String getCookieName();


    public void pageBeginRender(PageEvent event) {
        String error = buildErrorMessage(getSessionTimeOutMessage(), getSecureIdErrorMessage());
        setErrorMessage(error);
        setWarningMessage("Please note: This is a test environment to test the new automation system at IANA. ");
        setSessionTimeOutMessage(null);
        setSecureIdErrorMessage(null);
    }

    private String buildErrorMessage(String sessionTimeOutMessage, String secureIdErrorMessage) {
        StringBuilder builder = new StringBuilder();

        if (StringUtil.isNotBlank(secureIdErrorMessage)) {
            builder.append(secureIdErrorMessage);
        }

        if (StringUtil.isNotBlank(sessionTimeOutMessage)) {
            builder.append(sessionTimeOutMessage).append("\n");
        }

        if (StringUtil.isNotBlank(getErrorMessage())) {
            builder.append(getErrorMessage()).append("\n");
        }

        return StringUtil.isBlank(builder.toString()) ? null : builder.toString();
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

        if (delegate.getHasErrors()) {
            return null;
        }


        try {
            WebUser user = getAuthenticationService().login(getUserName(), password);
            return loginUser(user);
        }
        catch (AuthenticationRequiredException e) {
            if (e.getRequired() == Authentication.SECURID) {
                return redirectToSecureIdPage(e.getToken());
            }
            IValidationDelegate validationDelegate = getValidationDelegate();
            validationDelegate.record(e.getMessage(), null);
        }
        catch (PasswordExpiredException e) {
            BaseExpiredPasswordChange page = (BaseExpiredPasswordChange) getExpiredPasswordChange();
            page.setUserName(getUserName());
            page.setShowContinue(false);
            page.setWarningMessage(getMessageUtil().getFirstLoginMessage());
            page.activate();
        }
        catch (AuthenticationException ex) {
            IFormComponent field = getUserNameField();
            IValidationDelegate validationDelegate = getValidationDelegate();
            validationDelegate.setFormComponent(field);
            validationDelegate.record(ex.getMessage(), null);
        }
        return null;

    }

    protected ILink redirectToSecureIdPage(AuthenticationToken token) {
        IPage secureIdPage = getSecureIdPage();
        ExternalServiceParameter parameter =
                new ExternalServiceParameter(secureIdPage.getPageName(), new Object[]{getUserName(), token});
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

        if (isRememberMe()) {
            getCookieSource().writeCookieValue(getCookieName(), getUserName(), COOKIE_MAX_AGE);
        }

        cycle.forgetPage(getPageName());
        return iLink;
    }


    private IEngineService getService() {

        if (getCallback() == null) {
            return getHomeService();
        }

        if (!getCallback().isCallbackForUser(getVisitState().getUserId())) {
            setCallback(null);
            return getHomeService();
        }

        boolean external = getCallback().isExternal();

        if (external) {
            return getExternalPageService();
        }

        return getPageService();
    }


}


