package org.iana.rzm.web.pages.admin;

import org.apache.commons.lang.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;

import java.util.*;

public abstract class EditSubDomain extends AdminPage
    implements SubDomainAttributeEditor, PageBeginRenderListener, IExternalPage {

    public static final String PAGE_NAME = "admin/EditSubDomain";

    @Component(id = "editor", type = "SubDomainEditor", bindings = {
        "editor=prop:editor", "registryUrl=prop:registryUrl", "whoisServer=prop:whoisServer"
        })
    public abstract IComponent getEditorComponent();

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:requestsPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "ShowPendingRequestsMessage",
               bindings = {"listener=listener:viewPendingRequests", "pendigRequestMessage=literal:There is Request Pending for this domain."}
    )
    public abstract IComponent getPendingRequestsMessageComponent();


    @InjectPage("admin/EditDomain")
    public abstract EditDomain getEditDomain();

    @InjectPage("admin/RequestsPerspective")
    public abstract RequestsPerspective getRequestsPerspective();

    @Persist("client:page")
    public abstract void setCallback(ICallback callback);
    public abstract ICallback getCallback();

    @Persist("client:page")
    public abstract long getDomainId();
    public abstract void setDomainId(long id);

    @Persist("client:page")
    public abstract String getOriginalWhoisServer();
    public abstract void setOriginalWhoisServer(String server);

    @Persist("client:page")
    public abstract String getOriginalRegistryUrl();
    public abstract void setOriginalRegistryUrl(String url);

    @Persist("client:page")
    public abstract void setRegistryUrl(String url);
    public abstract String getRegistryUrl();

    @Persist("client:page")
    public abstract void setWhoisServer(String server);
    public abstract String getWhoisServer();

    public abstract void setModifiedDomain(DomainVOWrapper domain);
    public abstract DomainVOWrapper getModifiedDomain();

    protected Object[] getExternalParameters() {
        return new Object[]{
            getDomainId(), getWhoisServer(),getRegistryUrl(), getCallback(), getModifiedDomain()
        };
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){
        if(parameters.length < 4){
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
        }
        Long domainId = (Long) parameters[0];
        setDomainId(domainId);
        setWhoisServer((String) parameters[1]);
        setRegistryUrl((String) parameters[2]);
        setCallback((ICallback) parameters[3]);
        try{
            restoreCurrentDomain(getDomainId());
            if(parameters.length > 5 && parameters[4] != null){
                restoreModifiedDomain((DomainVOWrapper) parameters[5]);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }
    }


    public void pageBeginRender(PageEvent event) {
        setModifiedDomain(getVisitState().getMmodifiedDomain());
        //try {
        //    if (getOriginalRegistryUrl() == null || getOriginalWhoisServer() == null) {
        //        SystemDomainVOWrapper domain = getAdminServices().getDomain(getDomainId());
        //        setOriginalRegistryUrl(domain.getRegistryUrl());
        //        setOriginalWhoisServer(domain.getWhoisServer());
        //    }
        //} catch (NoObjectFoundException e) {
        //    getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        //}
    }

    public SubDomainAttributeEditor getEditor() {
        return this;
    }

    public void save(String registryUrl, String whois) {
        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        if (StringUtils.equals(registryUrl, getOriginalRegistryUrl()) &&
            StringUtils.equals(whois, getOriginalWhoisServer())) {
            return;
        }
        domain.setRegistryUrl(registryUrl);
        domain.setWhoisServer(whois);
        getVisitState().markDomainDirty(getDomainId());
        getVisitState().storeDomain(domain);
        getCallback().performCallback(getRequestCycle());
    }

    public RequestsPerspective viewPendingRequests() {
        RequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new OpenTransactionForDomainsFetcher(Arrays.asList(getVisitState().getCurrentDomain(
            getDomainId()).getName()), getRzmServices()));
        return page;
    }


    public boolean isRequestsPending() {
        return getVisitState().getCurrentDomain(getDomainId()).isOperationPending();
    }

    public void revert() {
        getCallback().performCallback(getRequestCycle());
    }


}
