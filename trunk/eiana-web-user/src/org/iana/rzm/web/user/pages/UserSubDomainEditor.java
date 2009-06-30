package org.iana.rzm.web.user.pages;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.common.DomainChangeType;
import org.iana.rzm.web.common.model.DomainVOWrapper;
import org.iana.rzm.web.common.model.SystemDomainVOWrapper;
import org.iana.rzm.web.common.query.retriver.OpenTransactionForDomainsRetriver;
import org.iana.rzm.web.tapestry.editors.SubDomainAttributeEditor;

import java.util.Arrays;

public abstract class UserSubDomainEditor extends UserPage
    implements PageBeginRenderListener, SubDomainAttributeEditor, IExternalPage {

    public static final String PAGE_NAME = "UserSubDomainEditor";

    @Component(id = "currentDetails", type = "rzmLib:SubDomain", bindings = {
        "registryUrl=prop:registryUrl",
        "originalRegistryUrl=prop:originalRegistryUrl",
        "whoisServer=prop:whoisServer",
        "originalWhoisServer=prop:originalWhoisServer",
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

    @Persist("client")
    public abstract String getOriginalWhoisServer();
    public abstract void setOriginalWhoisServer(String server);

    @Persist("client")
    public abstract String getOriginalRegistryUrl();
    public abstract void setOriginalRegistryUrl(String url);

    @Persist("client")
    public abstract String getWhoisServer();
    public abstract void setWhoisServer(String whois);

    @Persist("client")
    public abstract String getRegistryUrl();
    public abstract void setRegistryUrl(String url);

    public void pageBeginRender(PageEvent event) {

        setModifiedDomain(getVisitState().getModifiedDomain(getDomainId()));

        try{
            if (getOriginalRegistryUrl() == null || getOriginalWhoisServer() == null) {
                    SystemDomainVOWrapper domain = getUserServices().getDomain(getDomainId());
                    setOriginalRegistryUrl(domain.getRegistryUrl());
                    setOriginalWhoisServer(domain.getWhoisServer());
                }
            } catch (NoObjectFoundException e) {
                getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
            }
    }

    @SuppressWarnings("unchecked")
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {

            if(parameters.length < 4){
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
        }
        Long domainId = (Long) parameters[0];
        setDomainId(domainId);
        setWhoisServer((String) parameters[1]);
        setRegistryUrl((String) parameters[2]);

        try{
            restoreCurrentDomain(getDomainId());
            if(parameters.length > 5 && parameters[4] != null){
                restoreModifiedDomain((DomainVOWrapper) parameters[5]);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        }
    }


    public SubDomainAttributeEditor getEditor() {
        return this;
    }

    public void save(String registryUrl, String whois) {
        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        domain.setRegistryUrl(registryUrl);
        domain.setWhoisServer(whois);


        if (StringUtils.equals(registryUrl, getOriginalRegistryUrl()) &&
            StringUtils.equals(whois, getOriginalWhoisServer())) {
            getVisitState().clearChange(domain.getId(), DomainChangeType.sudomain);
        }else{
            getVisitState().markDomainDirty(getDomainId(), DomainChangeType.sudomain);
            getVisitState().storeDomain(domain);
        }
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
