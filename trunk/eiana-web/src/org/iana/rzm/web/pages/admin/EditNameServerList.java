package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.apache.tapestry.form.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.tapestry.*;
import org.iana.rzm.web.util.*;

import java.util.*;

public abstract class EditNameServerList extends AdminPage implements PageBeginRenderListener, NameServerAttributesEditor {

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:isRequestPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "ShowPendingRequestsMessage",
               bindings = {"listener=listener:viewPendingRequests", "pendigRequestMessage=literal:There is Request Pending for this domain."}
    )
    public abstract IComponent getPendingRequestsMessageComponent();

    @Component(id = "editor", type = "NameServerListEditor", bindings = {
        "editor=prop:editor", "list=prop:nameServerListValue"})
    public abstract IComponent getEditorComponent();

    @Bean
    public abstract JavaScriptDelegator getDeligate();

    @InjectPage("admin/EditDomain")
    public abstract EditDomain getEditDomain();

    @InjectPage("admin/RequestsPerspective")
    public abstract RequestsPerspective getRequestsPerspective();

    @Persist("client:page")
    public abstract List<NameServerValue> getNameServerListValue();

    public abstract void setNameServerListValue(List<NameServerValue> list);

    @Persist("client:page")
    public abstract long getDomainId();

    public abstract void setDomainId(long domainId);

    @Persist("client:page")
    public abstract DomainVOWrapper getModifiedDomain();

    public abstract void setModifiedDomain(DomainVOWrapper domain);

    public List<NameServerValue> getCurrentNameServers() {
        return WebUtil.convert(getVisitState().getCurrentDomain(getDomainId()).getNameServers());
    }

    public NameServerAttributesEditor getEditor() {
        return this;
    }

    public void pageBeginRender(PageEvent event) {
        setModifiedDomain(getVisitState().getMmodifiedDomain());
        if (getNameServerListValue() == null) {
            List<NameServerValue> list = getNameServersForEdit();
            setNameServerListValue(list);
        }
    }

    private List<NameServerValue> getNameServersForEdit() {
        List<NameServerVOWrapper> nameServers = getVisitState().getCurrentDomain(getDomainId()).getNameServers();
        List<NameServerValue> list = new ArrayList<NameServerValue>();
        for (NameServerVOWrapper wrapper : nameServers) {
            list.add(new NameServerValue(wrapper));
        }
        return list;
    }

    public void save(List<NameServerValue> list) {
        List<NameServerVOWrapper> nameServers = new ArrayList<NameServerVOWrapper>();
        for (NameServerValue nameServerValue : list) {
            nameServers.add(nameServerValue.asNameServer());
        }

        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        domain.updateNameServers(nameServers);
        getVisitState().markDomainDirty(getDomainId());
        setNameServerListValue(null);
        EditDomain editDomain = getEditDomain();
        editDomain.setDomainId(getDomainId());
        getRequestCycle().activate(editDomain);
    }


    public void revert() {
        setNameServerListValue(null);
        EditDomain editDomain = getEditDomain();
        editDomain.setDomainId(getDomainId());
        getRequestCycle().activate(editDomain);
    }

    public boolean getIsRequestPending() {
        return getVisitState().getCurrentDomain(getDomainId()).isOperationPending();
    }

    public RequestsPerspective viewPendingRequests() {
        RequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new TransactionForDomainFetcher(getModifiedDomain().getName(), getRzmServices()));
        return page;
    }

    public void setErrorField(IFormComponent field, String message) {
        super.setErrorField(field.getId(), message);
    }

}
