package org.iana.rzm.web.pages.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;

public abstract class UserSubDomainEditor extends UserPage
    implements PageBeginRenderListener, SubDomainAttributeEditor, IExternalPage {

    @Component(id = "currentDetails", type = "SubDomain", bindings = {
        "registryUrl=prop:originalDomain.registryUrl",
        "originalRegistryUrl=prop:originalDomain.registryUrl",
        "whoisServer=prop:originalDomain.whoisServer",
        "originalWhoisServer=prop:originalDomain.whoisServer",
        "listener=listener:editSubDomain",
        "editible=literal:false"
        })
    public abstract IComponent getSubDomainComponent();

    @Component(id = "editor", type = "SubDomainEditor", bindings = {
        "editor=prop:editor", "registryUrl=prop:registryUrl", "whoisServer=prop:whoisServer"
        })
    public abstract IComponent getEditorComponent();

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:requestsPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "ShowPendingRequestsMessage",
               bindings = {"listener=listener:viewPendingRequests"}
    )
    public abstract IComponent getPendingRequestsMessageComponent();

    @InjectPage("user/ReviewDomain")
    public abstract ReviewDomain getReviewDomain();

    @InjectPage("user/UserRequestsPerspective")
    public abstract UserRequestsPerspective getRequestsPerspective();

    @Persist("client:page")
    public abstract DomainVOWrapper getModifiedDomain();

    public abstract void setModifiedDomain(DomainVOWrapper domain);

    @Persist("client:page")
    public abstract long getDomainId();

    public abstract void setDomainId(long domain);

    public abstract void setOriginalDomain(DomainVOWrapper domain);

    public abstract DomainVOWrapper getOriginalDomain();

    public abstract String getWhoisServer();

    public abstract void setWhoisServer(String whois);

    public abstract String getRegistryUrl();

    public abstract void setRegistryUrl(String url);

    public void pageBeginRender(PageEvent event) {

        setModifiedDomain(getVisitState().getMmodifiedDomain());

        try {
            if (getOriginalDomain() == null) {
                setOriginalDomain(getUserServices().getDomain(getDomainId()));
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        }
    }

    @SuppressWarnings("unchecked")
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {

        if (parameters.length == 0 || parameters.length < 1) {
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        setDomainId((Long) parameters[0]);
        try {
            restoreCurrentDomain(getDomainId());
            if (parameters.length == 2) {
                restoreModifiedDomain((DomainVOWrapper) parameters[1]);
            }
        } catch (NoObjectFoundException e) {
            getExternalPageErrorHandler().handleExternalPageError("System Error restoring session");
        }
    }


    public SubDomainAttributeEditor getEditor() {
        return this;
    }

    public void save(String registryUrl, String whois) {
        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        domain.setRegistryUrl(registryUrl);
        domain.setWhoisServer(whois);
        getVisitState().markDomainDirty(getDomainId());
        backToRevewDomainPage();
    }

    public void revert() {
        backToRevewDomainPage();
    }

    private void backToRevewDomainPage() {
        ReviewDomain reviewDomain = getReviewDomain();
        reviewDomain.setDomainId(getDomainId());
        getRequestCycle().activate(reviewDomain);
    }

    public UserRequestsPerspective viewPendingRequests() {
        UserRequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(
            new TransactionForDomainFetcher(
                getVisitState().getCurrentDomain(getDomainId()).getName(), getUserServices()));
        return page;
    }


    public boolean isRequestsPending() {
        return getVisitState().getCurrentDomain(getDomainId()).isOperationPending();
    }

    protected Object[] getExternalParameters() {
        DomainVOWrapper domain = getModifiedDomain();
        if (domain != null) {
            return new Object[]{getDomainId(), domain};
        }
        return new Object[]{getDomainId()};
    }

}
