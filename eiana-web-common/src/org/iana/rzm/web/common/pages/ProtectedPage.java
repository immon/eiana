package org.iana.rzm.web.common.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.apache.tapestry.web.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.callback.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.services.*;
import org.iana.rzm.web.tapestry.components.contact.*;
import org.iana.web.tapestry.session.*;

import javax.servlet.http.*;

public abstract class ProtectedPage extends RzmPage implements PageValidateListener {

    @InjectPage(BaseLogin.PAGE_NAME)
    public abstract BaseLogin getLogin();

    @InjectObject("service:rzm.ObjectNotFoundHandler")
    public abstract ObjectNotFoundHandler getObjectNotFoundHandler();

    @InjectObject("service:rzm.AccessDeniedHandler")
    public abstract AccessDeniedHandler getAccessDeniedHandler();

    @InjectObject("service:rzm.QueryExceptionHandler")
    public abstract QueryExceptionHandler getQueryExceptionHandler();

    @InjectObject("service:tapestry.globals.WebRequest")
    public abstract WebRequest getWebRequest();

    @InjectObject("service:tapestry.globals.HttpServletRequest")
    public abstract HttpServletRequest getHttpRequest();

    @InjectObject("service:rzm.ApplicationLifecycle")
    public abstract ApplicationLifecycle getApplicationLifecycle();

    @InjectObject("service:rzm.ContactServices")
    public abstract ContactServices getContactServices();

    @InjectState("visit")
    public abstract Visit getVisitState();

    @InjectStateFlag("visit")
    public abstract boolean getVisitStateExists();

    @InjectPage(BaseGeneralError.PAGE_NAME)
    public abstract IPage getErrorPage();

    public abstract RzmServices getRzmServices();

    protected abstract String getErrorPageName();

    @Persist("client")
    public abstract long getLogedInUserId();

    public abstract void setLogedInUserId(long id);

    public RzmCallback createCallback() {
        if (isExternal()) {
            return new RzmCallback(getPageName(), isExternal(), getExternalParameters(), getLogedInUserId());
        }

        return new RzmCallback(getPageName(), getLogedInUserId());
    }

    public void pageValidate(PageEvent event) {
        if (isUserLoggedIn()) {
            setLogedInUserId(getVisitState().getUserId());
            return;
        }

        BaseLogin login = getLogin();

        String state = findState();
        if (state != null) {
            login.setSessionTimeOutMessage(getMessageUtil().getSessionTimeoutMessage());
            if (isExternal()) {
                RzmCallback callback =
                    new RzmCallback(getPageName(), isExternal(), getExternalParameters(), getLogedInUserId());
                login.setCallback(callback);
            }
        }

        throw new PageRedirectException(login);

    }

    public boolean isValidCountryCode(String code) {
        return getRzmServices().isValidCountryCode(code);
    }

    public IPage logout() {
        getApplicationLifecycle().logout();
        return getLogin();
    }

    public void preventResubmission() {

    }

    protected Object[] getExternalParameters() {
        return null;
    }


    protected void restoreModifiedDomain(DomainVOWrapper domain) throws NoObjectFoundException {
        getVisitState().markAsVisited(domain);
        TransactionActionsVOWrapper transactionActionsVOWrapper = null;
        try {
            transactionActionsVOWrapper = getRzmServices().getChanges(domain);
            if (transactionActionsVOWrapper.getChanges().size() > 0) {
                if (getVisitState().getModifiedDomain(domain.getId()) == null) {
                    getVisitState().storeDomain(domain);
                }
            }

            getVisitState().storeDomain(domain);

            for (ActionVOWrapper actionVOWrapper : transactionActionsVOWrapper.getChanges()) {

                if (actionVOWrapper.getTitle().startsWith("modify name servers")) {
                    getVisitState().markDomainDirty(domain.getId(), DomainChangeType.ns);
                } else if (actionVOWrapper.getTitle().contains(DomainChangeType.Administrative.getDisplayName())) {
                    getVisitState().markDomainDirty(domain.getId(), DomainChangeType.Administrative);
                } else if (actionVOWrapper.getTitle().contains(DomainChangeType.Technical.getDisplayName())) {
                    getVisitState().markDomainDirty(domain.getId(), DomainChangeType.Technical);
                } else if (actionVOWrapper.getTitle().contains(DomainChangeType.SO.getDisplayName())) {
                    getVisitState().markDomainDirty(domain.getId(), DomainChangeType.SO);
                } else {
                    getVisitState().markDomainDirty(domain.getId(), DomainChangeType.sudomain);
                }
            }
        }

        catch (RadicalAlterationException e) {
            setErrorMessage(getMessageUtil().getRadicalAlterationCheckMessage(e.getDomainName()));
        } catch (SharedNameServersCollisionException e) {
            setErrorMessage(getMessageUtil().getSharedNameServersCollisionMessage(e.getNameServers()));
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

    protected final boolean isUserLoggedIn() {
        return getVisitStateExists() && getVisitState().isUserLoggedIn();
    }


    protected boolean isExternal() {
        return IExternalPage.class.isAssignableFrom(this.getClass());
    }

    private String findState() {
        String state = getRequestCycle().getInfrastructure().getRequest().getParameterValue("state:" + getPageName());
        if (state != null) {
            return state;
        }
        return getRequestCycle().getInfrastructure().getRequest().getParameterValue("form:" + getPageName());
    }
}

