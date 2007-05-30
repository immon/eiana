package org.iana.rzm.web.pages;

import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.PageRedirectException;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.InjectState;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.event.PageValidateListener;
import org.apache.tapestry.web.WebRequest;
import org.iana.rzm.web.services.ExternalPageErrorHandler;
import org.iana.rzm.web.services.ObjectNotFoundHandler;
import org.iana.rzm.web.services.RzmServices;
import org.iana.rzm.web.services.TokenSynchronizer;
import org.iana.rzm.web.tapestry.RzmCallback;

import javax.servlet.http.HttpServletRequest;

public abstract class Protected extends RzmPage implements  PageValidateListener {

    @InjectPage("Login")
    public abstract Login getLogin();

    //@InjectObject("service:rzm.TokenSynchronizer")
    @InjectState("TokenSynchronizer")
    public abstract TokenSynchronizer getTokenSynchronizer();

    @InjectObject("service:rzm.ObjectNotFoundHandler")
    public abstract ObjectNotFoundHandler getObjectNotFoundHandler();

    @InjectObject("service:rzm.ExternalPageErrorHandler")
    public abstract ExternalPageErrorHandler getExternalPageErrorHandler();

    @InjectObject("service:tapestry.globals.WebRequest")
    public abstract WebRequest getWebRequest();

    @InjectObject("service:tapestry.globals.HttpServletRequest")
    public abstract HttpServletRequest getHttpRequest();

    public abstract RzmServices getRzmServices();

    protected Object[] getExternalParameters() {
        return null;
    }

    public void pageValidate(PageEvent event) {

        if (isUserLoggedIn()) {
            return;
        }

        Login login = getLogin();

        String state = getRequestCycle().getInfrastructure().getRequest().getParameterValue("state:" + getPageName());
        if (state != null) {
            login.setSessionTimeOutMessage("Your Session has time out. Please login to continue");
            if (isExternal()) {
                RzmCallback callback = new RzmCallback(getPageName(), isExternal(), getExternalParameters());
                login.setCallback(callback);
            }
        }

        throw new PageRedirectException(login);

    }


    public void preventResubmission() {
        getTokenSynchronizer().isResubmission();
    }

    protected final boolean isUserLoggedIn() {
        return getVisitStateExists() && getVisitState().isUserLoggedIn();
    }

    protected boolean isExternal() {
        return IExternalPage.class.isAssignableFrom(this.getClass());
    }

    protected boolean hasErrors() {
        return getValidationDelegate().getHasErrors();
    }

}
