package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IFormComponent;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.common.DomainChangeType;
import org.iana.rzm.web.common.model.DomainVOWrapper;
import org.iana.rzm.web.common.model.NameServerVOWrapper;
import org.iana.rzm.web.common.model.NameServerValue;
import org.iana.rzm.web.common.query.retriver.OpenTransactionForDomainsRetriver;
import org.iana.rzm.web.common.utils.WebUtil;
import org.iana.rzm.web.tapestry.editors.NameServerAttributesEditor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class EditNameServerList extends AdminPage implements PageBeginRenderListener, NameServerAttributesEditor, IExternalPage {

    public static final String PAGE_NAME = "EditNameServerList";

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:isRequestPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "rzmLib:ShowPendingRequestsMessage",
               bindings = {"listener=listener:viewPendingRequests", "pendigRequestMessage=literal:There is Request Pending for this domain."}
    )
    public abstract IComponent getPendingRequestsMessageComponent();

    @Component(id = "editor", type = "rzmLib:NameServerListEditor", bindings = {
        "editor=prop:editor", "list=prop:nameServerListValue", "domainId=prop:domainId"})
    public abstract IComponent getEditorComponent();

    
    @InjectPage(EditDomain.PAGE_NAME)
    public abstract EditDomain getEditDomain();

    @InjectPage(RequestsPerspective.PAGE_NAME)
    public abstract RequestsPerspective getRequestsPerspective();

    @Persist("client")
    public abstract List<NameServerValue> getNameServerListValue();
    public abstract void setNameServerListValue(List<NameServerValue> list);

    @Persist("client")
    public abstract void setCallback(ICallback callback);
    public abstract ICallback getCallback();

    @Persist("client")
    public abstract long getDomainId();
    public abstract void setDomainId(long domainId);

    @Persist("client")
    public abstract DomainVOWrapper getModifiedDomain();
    public abstract void setModifiedDomain(DomainVOWrapper domain);

    @Persist("client")
    public abstract  List<NameServerVOWrapper> getOriginalNameServers();
    public abstract void setOriginalNameServers( List<NameServerVOWrapper> list);

    public List<NameServerValue> getCurrentNameServers() {
        return WebUtil.convert(getVisitState().getCurrentDomain(getDomainId()).getNameServers());
    }

    public NameServerAttributesEditor getEditor() {
        return this;
    }


    protected Object[] getExternalParameters() {
        return new Object[]{
            getDomainId(), getNameServerListValue(), getCallback(), getModifiedDomain(), getOriginalNameServers()
        };
    }

    @SuppressWarnings("unchecked")
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length < 3) {
            getExternalPageErrorHandler().handleExternalPageError(
                getMessageUtil().getSessionRestorefailedMessage());
        }
        Long domainId = (Long) parameters[0];
        setDomainId(domainId);
        setNameServerListValue((List<NameServerValue>) parameters[1]);
        setCallback((ICallback) parameters[2]);
        try {
            restoreCurrentDomain(getDomainId());
            if (parameters.length > 3 && parameters[3] != null) {
                restoreModifiedDomain((DomainVOWrapper) parameters[3]);
            }

        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        }

        if(parameters.length > 4 && parameters[4] != null){
            setOriginalNameServers((List<NameServerVOWrapper>) parameters[4]);
        }
    }

    public void pageBeginRender(PageEvent event) {
        setModifiedDomain(getVisitState().getModifiedDomain(getDomainId()));
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
        List<NameServerVOWrapper> nameServers = WebUtil.convertToVos(list);
        DomainVOWrapper domain = getVisitState().getCurrentDomain(getDomainId());
        domain.updateNameServers(nameServers);
        List<NameServerVOWrapper> oldList = getOriginalNameServerList(getDomainId());

        if (WebUtil.isModefied(oldList, WebUtil.convertToVos(list))) {
            getVisitState().markDomainDirty(getDomainId(), DomainChangeType.ns);
        } else {
            getVisitState().clearChange(getDomainId(), DomainChangeType.ns);
        }

        getVisitState().storeDomain(domain);
        setNameServerListValue(null);
        getCallback().performCallback(getRequestCycle());
    }

    public List<NameServerVOWrapper> getOriginalNameServerList(long domainId) {
        try {
            if(getOriginalNameServers() == null){
                return getAdminServices().getDomain(domainId).getNameServers();
            }

            return getOriginalNameServers();
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        }

        return new ArrayList<NameServerVOWrapper>();
    }


    public void revert() {
        setNameServerListValue(null);
        getCallback().performCallback(getRequestCycle());
    }

    public boolean getIsRequestPending() {
        return getVisitState().getCurrentDomain(getDomainId()).isOperationPending();
    }

    public RequestsPerspective viewPendingRequests() {
        RequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new OpenTransactionForDomainsRetriver(Arrays.asList(getVisitState().getCurrentDomain(
            getDomainId()).getName()), getRzmServices()));
        return page;
    }

    public void setErrorField(IFormComponent field, String message) {
        super.setErrorField(field.getId(), message);
    }


}
