package org.iana.rzm.web.pages.user;

import org.apache.log4j.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.apache.tapestry.form.*;
import org.iana.rzm.common.validators.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.tapestry.*;
import org.iana.rzm.web.util.*;

import java.util.*;

public abstract class UserNameServerListEditor extends UserPage implements PageBeginRenderListener,
        NameServerAttributesEditor, IExternalPage {

    public static final String PAGE_NAME = "user/UserNameServerListEditor";
    public static final Logger LOGGER = Logger.getLogger(UserNameServerListEditor.class.getName());

    @Component(id = "currentDetails", type = "ListNameServers", bindings = {
            "domainId=prop:domainId", "nameServers=prop:currentNameServers", "editible=literal:false"})
    public abstract IComponent getCurrentDetailsComponent();

    @Component(id = "pendingRequests", type = "If", bindings = {"condition=prop:isRequestPending"})
    public abstract IComponent getPendingRequestsComponent();

    @Component(id = "pendingRequestsMessage", type = "ShowPendingRequestsMessage", bindings = {
            "listener=listener:viewPendingRequests"})
    public abstract IComponent getPendingRequestsMessageComponent();

    @Component(id = "editor", type = "NameServerListEditor", bindings = {
            "editor=prop:editor", "list=prop:nameServerListValue", "domainId=prop:domainId"})
    public abstract IComponent getEditorComponent();

    @Bean
    public abstract JavaScriptDelegator getDeligate();

    @InjectPage("user/ReviewDomain")
    public abstract ReviewDomain getReviewDomainPage();

    @InjectPage("user/UserRequestsPerspective")
    public abstract UserRequestsPerspective getRequestsPerspective();

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
        try {
            return WebUtil.convert(getUserServices().getDomain(getDomainId()).getNameServers());
        } catch (NoObjectFoundException e) {
            log(LOGGER, "NoObjectFoundException", e);
            return new ArrayList<NameServerValue>();
        }catch(AccessDeniedException e){
            log(LOGGER, "AccessDeniedException", e);
            return new ArrayList<NameServerValue>();
        }
    }

    public NameServerAttributesEditor getEditor() {
        return this;
    }

    public void pageBeginRender(PageEvent event) {
        setModifiedDomain(getVisitState().getModifiedDomain(getDomainId()));

        if (getNameServerListValue() == null) {
            List<NameServerValue> list = getNameServersForEdit();
            setNameServerListValue(list);
        }

    }


    @SuppressWarnings("unchecked")
    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        CheckTool.checkNull(parameters, "Session Parameters");

        if(parameters.length == 0 || parameters.length < 2){
            getExternalPageErrorHandler().handleExternalPageError("Missing parameters for page");
            return;
        }

        setNameServerListValue((List<NameServerValue>)parameters[0]);
        setDomainId((Long)parameters[1]);
        try {
            restoreCurrentDomain(getDomainId());
            if(parameters.length == 3){
                restoreModifiedDomain((DomainVOWrapper) parameters[2]);
            }
        } catch (NoObjectFoundException e) {
            getExternalPageErrorHandler().handleExternalPageError("Can not restore session");
            LOGGER.warn("NoObjectFoundException ", e);
        }
    }

    protected Object[] getExternalParameters() {
        DomainVOWrapper domain = getModifiedDomain();
        if(domain != null){
            return new Object[]{getNameServerListValue(), getDomainId(), domain};
        }
        return new Object[]{getNameServerListValue(), getDomainId()};
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

        if(WebUtil.isModefied(oldList, WebUtil.convertToVos(list))){
            getVisitState().markDomainDirty(getDomainId(),DomainChangeType.ns);
            getVisitState().storeDomain(domain);
        }else{
            getVisitState().clearChange(getDomainId(),DomainChangeType.ns);    
        }

        setNameServerListValue(null);
        ReviewDomain reviewDomainPage = getReviewDomainPage();
        reviewDomainPage.setDomainId(getDomainId());
        getRequestCycle().activate(reviewDomainPage);
    }

    public List<NameServerVOWrapper> getOriginalNameServerList(long domainId) {
        try {
            return getUserServices().getDomain(domainId).getNameServers();
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        }
        return new ArrayList<NameServerVOWrapper>();
    }


    public void revert() {
        setNameServerListValue(null);
        ReviewDomain reviewDomain = getReviewDomainPage();
        reviewDomain.setDomainId(getDomainId());
        getRequestCycle().activate(reviewDomain);
    }

    public boolean getIsRequestPending() {
        return getVisitState().getCurrentDomain(getDomainId()).isOperationPending();
    }

    public UserRequestsPerspective viewPendingRequests() {
        UserRequestsPerspective page = getRequestsPerspective();
        page.setEntityFetcher(new OpenTransactionForDomainsFetcher(Arrays.asList(getVisitState().getCurrentDomain(getDomainId()).getName()), getUserServices()));
        page.setCallback(createCallback());
        return page;
    }

    public void setErrorField(IFormComponent field, String message) {
        super.setErrorField(field.getId(), message);
    }




}
