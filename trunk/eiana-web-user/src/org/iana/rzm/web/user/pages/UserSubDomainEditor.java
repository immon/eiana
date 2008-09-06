package org.iana.rzm.web.user.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.rzm.web.editors.*;

import java.util.*;

public abstract class UserSubDomainEditor extends UserPage
    implements PageBeginRenderListener, SubDomainAttributeEditor, IExternalPage {

    public static final String PAGE_NAME = "UserSubDomainEditor";

    @Component(id = "currentDetails", type = "rzmLib:SubDomain", bindings = {
        "registryUrl=prop:originalDomain.registryUrl",
        "originalRegistryUrl=prop:originalDomain.registryUrl",
        "whoisServer=prop:originalDomain.whoisServer",
        "originalWhoisServer=prop:originalDomain.whoisServer",
        "listener=listener:editSubDomain",
        "editible=literal:false"
        })
    public abstract IComponent getSubDomainComponent();

    @Component(id = "editor", type = "rzmLib:SubDomainEditor", bindings = {
        "editor=prop:editor", "registryUrl=prop:registryUrl", "whoisServer=prop:whoisServer"
        })
    public abstract IComponent getEditorComponent();

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:requestsPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "rzmLib:ShowPendingRequestsMessage",
               bindings = {"listener=listener:viewPendingRequests"}
    )
    public abstract IComponent getPendingRequestsMessageComponent();

    @InjectPage(ReviewDomain.PAGE_NAME)
    public abstract ReviewDomain getReviewDomain();

    @InjectPage(UserRequestsPerspective.PAGE_NAME)
    public abstract UserRequestsPerspective getRequestsPerspective();

    @Persist("client")
    public abstract DomainVOWrapper getModifiedDomain();
    public abstract void setModifiedDomain(DomainVOWrapper domain);

    @Persist("client")
    public abstract long getDomainId();
    public abstract void setDomainId(long domain);

    public abstract void setOriginalDomain(DomainVOWrapper domain);
    public abstract DomainVOWrapper getOriginalDomain();

    public abstract String getWhoisServer();
    public abstract void setWhoisServer(String whois);

    public abstract String getRegistryUrl();
    public abstract void setRegistryUrl(String url);

    public void pageBeginRender(PageEvent event) {

        setModifiedDomain(getVisitState().getModifiedDomain(getDomainId()));

        try {
            if (getOriginalDomain() == null) {
                setOriginalDomain(getUserServices().getDomain(getDomainId()));
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
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
        getVisitState().markDomainDirty(getDomainId(), DomainChangeType.sudomain);
        getVisitState().storeDomain(domain);
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
        page.setEntityFetcher(new OpenTransactionForDomainsRetriver(
            Arrays.asList(getVisitState().getCurrentDomain(getDomainId()).getName()), getUserServices()));
        page.setCallback(createCallback());
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
