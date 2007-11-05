package org.iana.rzm.web.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.apache.tapestry.web.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.tapestry.*;

import javax.servlet.http.*;

public abstract class Protected extends RzmPage implements PageValidateListener {

    @InjectPage("Login")
    public abstract Login getLogin();

    //@InjectObject("service:rzm.TokenSynchronizer")
    @InjectState("TokenSynchronizer")
    public abstract TokenSynchronizer getTokenSynchronizer();

    @InjectObject("service:rzm.ObjectNotFoundHandler")
    public abstract ObjectNotFoundHandler getObjectNotFoundHandler();

    @InjectObject("service:rzm.AccessDeniedHandler")
    public abstract AccessDeniedHandler getAccessDeniedHandler();

    @InjectObject("service:tapestry.globals.WebRequest")
    public abstract WebRequest getWebRequest();

    @InjectObject("service:tapestry.globals.HttpServletRequest")
    public abstract HttpServletRequest getHttpRequest();

    @InjectState("visit")
    public abstract Visit getVisitState();

    @InjectStateFlag("visit")
    public abstract boolean getVisitStateExists();

    public abstract RzmServices getRzmServices();

    protected abstract String getErrorPageName();

    @Persist("client:page")
    public abstract long getUserId();
    public abstract void setUserId(long id);

    protected Object[] getExternalParameters() {
        return null;
    }

    public RzmCallback createCallback() {
        if (isExternal()) {
            return new RzmCallback(getPageName(), isExternal(), getExternalParameters(), getUserId());
        }

        return new RzmCallback(getPageName(), getUserId());
    }

    public void pageValidate(PageEvent event) {

        if (isUserLoggedIn()) {
            setUserId(getVisitState().getUserId());
            return;
        }

        Login login = getLogin();

        String state = findState();
        if (state != null) {
            login.setSessionTimeOutMessage("Your Session has time out. Please login to continue");
            if (isExternal()) {
                RzmCallback callback = new RzmCallback(getPageName(), isExternal(), getExternalParameters(), getUserId());
                login.setCallback(callback);
            }
        }

        throw new PageRedirectException(login);

    }

    private String findState() {
        String state = getRequestCycle().getInfrastructure().getRequest().getParameterValue("state:" + getPageName());
        if (state != null) {
            return state;
        }
        return getRequestCycle().getInfrastructure().getRequest().getParameterValue("form:" + getPageName());
    }


    protected void restoreModifiedDomain(DomainVOWrapper domain) throws NoObjectFoundException {
        getVisitState().markAsVisited(domain);
        TransactionActionsVOWrapper transactionActionsVOWrapper = getRzmServices().getChanges(domain);
        if (transactionActionsVOWrapper.getChanges().size() > 0) {
            if(getVisitState().getMmodifiedDomain() == null){
                 getVisitState().storeDomain(domain);
            }
            getVisitState().markDomainDirty(domain.getId());
        }
    }

    protected void restoreCurrentDomain(long domainId) throws NoObjectFoundException {
        try {
            DomainVOWrapper wrapper = getRzmServices().getDomain(domainId);
            getVisitState().storeDomain(wrapper);
        } catch (AccessDeniedException e) {
            getAccessDeniedHandler().handleAccessDenied(e, getErrorPageName());
        }
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
}
