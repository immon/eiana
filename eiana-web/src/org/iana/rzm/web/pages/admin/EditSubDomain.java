package org.iana.rzm.web.pages.admin;

import org.apache.commons.lang.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;

public abstract class EditSubDomain extends AdminPage implements SubDomainAttributeEditor, PageBeginRenderListener {

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
    public abstract long getDomainId();

    public abstract void setDomainId(long id);

    @Persist("client:page")
    public abstract String getOriginalWhoisServer();

    public abstract void setOriginalWhoisServer(String server);

    @Persist("client:page")
    public abstract String getOriginalRegistryUrl();

    public abstract void setOriginalRegistryUrl(String url);


    public abstract void setRegistryUrl(String url);

    public abstract void setWhoisServer(String server);

    public abstract void setModifiedDomain(DomainVOWrapper domain);

    public void pageBeginRender(PageEvent event) {
        setModifiedDomain(getVisitState().getMmodifiedDomain());
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

        EditDomain editDomain = getEditDomain();
        editDomain.setDomainId(getDomainId());
        getRequestCycle().activate(editDomain);
    }

    public RequestsPerspective viewPendingRequests() {
        RequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new TransactionForDomainFetcher(getVisitState().getCurrentDomain(getDomainId()).getName(),
                                                              getRzmServices()));
        return page;
    }


    public boolean isRequestsPending() {
        return getVisitState().getCurrentDomain(getDomainId()).isOperationPending();
    }

    public void revert() {
        EditDomain editDomain = getEditDomain();
        editDomain.setDomainId(getDomainId());
        getRequestCycle().activate(editDomain);
    }
}
