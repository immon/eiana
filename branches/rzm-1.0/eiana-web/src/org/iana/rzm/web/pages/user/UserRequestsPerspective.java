package org.iana.rzm.web.pages.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.event.*;
import org.iana.criteria.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.model.criteria.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.services.user.*;


public abstract class UserRequestsPerspective extends UserPage
    implements PageBeginRenderListener, SortFactory, IExternalPage {

    public static final String PAGE_NAME = "user/UserRequestsPerspective";

    @Component(id = "listRequests", type = "ListRequests", bindings = {
        "entityQuery=prop:entityQuery",
        "listener=listener:viewRequestDetails",
        "linkTragetPage=prop:reviewDomainPage",
        "cancelRequestPage=literal:user/WithdrawRequest",
        "sortFactory=prop:sortFactory"
        })
    public abstract IComponent getListRequestComponent();

    @Component(id = "listImpactedpartRequests", type = "ImpactedPartiesListRequest", bindings = {
        "entityQuery=prop:entityQuery",
        "listener=listener:viewRequestDetails",
        "usePagination=literal:false",
        "noRequestMsg=literal:'There are no outstanding requests."
        }
    )
    public abstract IComponent getListImpactedPartyRequestComponent();

    @Component(id="back", type="DirectLink", bindings = {
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
        "listener=listener:back"
        })
    public abstract IComponent getBackComponent();

    @Component(id="useListReqiest", type="If", bindings = {"condition=prop:useListRequest"})
    public abstract IComponent getUseListRequestComponent();

    @Component(id="useImpactedParty", type="Else" )
    public abstract IComponent getUseImpactedPartyComponent();

    @Component(id= "showBack", type="If", bindings = {"condition=prop:showBackLink", "element=literal:table"})
    public abstract IComponent getShowBackComponent();

    @Bean(PaginatedEntityQuery.class)
    public abstract PaginatedEntityQuery getPaginatedEntityBean();

    @InjectPage("user/RequestInformation")
    public abstract RequestInformation getRequestInformation();

    @InjectPage("user/ReviewDomain")
    public abstract ReviewDomain getReviewDomainPage();

    @Persist("client")
    public abstract EntityFetcher getEntityFetcher();
    public abstract void setEntityFetcher(EntityFetcher fetcher);

    @Persist("client")
    public abstract void setSortField(SortOrder sortOrder);
    public abstract SortOrder getSortField();

    @Persist("client")
    public abstract ICallback getCallback();
    public abstract void setCallback(ICallback callback);

    @Persist("client")
    public abstract boolean isImpactedParty();
    public abstract void setImpactedParty(boolean b);

    public boolean isShowBackLink(){
        return getCallback() != null;
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length == 0 || parameters.length < 2) {
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        EntityFetcher entityFetcher = (EntityFetcher) parameters[0];
        SortOrder sortOrder = (SortOrder) parameters[1];
        setEntityFetcher(entityFetcher);
        setSortField(sortOrder);

        if(parameters.length > 2){
            setImpactedParty(Boolean.valueOf(parameters[2].toString()));
        }

        if (parameters.length > 3) {
            setCallback((ICallback) parameters[3]);
        }

    }


    protected Object[] getExternalParameters() {
        return getCallback() == null ?
               new Object[]{getEntityFetcher(), getSortField(),isImpactedParty()} :
               new Object[]{getEntityFetcher(), getSortField(), isImpactedParty(), getCallback()};
    }

    public SortFactory getSortFactory() {
        return this;
    }

    public boolean isUseListRequest(){
        return !isImpactedParty();
    }

    public void pageBeginRender(PageEvent event) {
        if (getEntityFetcher() == null) {
            setEntityFetcher(new TransactionFetcher(getUserServices()));
        }

        if (getSortField() == null) {
            setSortField(new SortOrder());
        }

        try {
            int count = getEntityQuery().getResultCount();
            Browser browser = getBrowser();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        } catch (AccessDeniedException e) {
            getAccessDeniedHandler().handleAccessDenied(e, UserGeneralError.PAGE_NAME);
        }
    }

    public void sort(String field, boolean accending) {
        setSortField(new SortOrder(field, accending));
    }

    public boolean isFieldSortable(String name) {
        return true;
    }

    public EntityQuery getEntityQuery() {
        PaginatedEntityQuery entityQuery = getPaginatedEntityBean();
        EntityFetcher entityFetcher = getEntityFetcher();
        entityFetcher.applySortOrder(getSortField());
        entityQuery.setFetcher(entityFetcher);
        return entityQuery;
    }

    public void viewRequestDetails(long requestId) {
        RequestInformation info = getRequestInformation();
        info.setImpactedThirdPartyView(isImpactedParty());
        info.setRequestId(requestId);
        info.setCallback(createCallback());
        getRequestCycle().activate(info);
    }

    public void back(){
        getCallback().performCallback(getRequestCycle());
    }

    protected Browser getBrowser() {
        if(isImpactedParty()){
            return ((ImpactedPartiesListRequest) getComponent("listImpactedpartRequests")).getRecords();            
        }
        return ((ListRequests) getComponent("listRequests")).getRecords();
    }

    private class TransactionFetcher implements EntityFetcher {
        private UserServices userServices;
        private Criterion criterion;
        private SortOrder sortOrder;

        public TransactionFetcher(UserServices userServices) {
            this.userServices = userServices;
            criterion = CriteriaBuilder.empty();
            sortOrder = new SortOrder();
        }


        public int getTotal() {
            return userServices.getTransactionCount(criterion);
        }

        public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
            return userServices.getTransactions(criterion, offset, length, sortOrder)
                .toArray(new PaginatedEntity[0]);
        }

        public void applySortOrder(SortOrder sortOrder) {
            this.sortOrder = sortOrder;
        }
    }

}
