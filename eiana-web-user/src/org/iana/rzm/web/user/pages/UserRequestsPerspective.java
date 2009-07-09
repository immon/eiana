package org.iana.rzm.web.user.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.criteria.Criterion;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.common.model.criteria.SortOrder;
import org.iana.rzm.web.common.query.PaginatedEntityQuery;
import org.iana.rzm.web.common.query.QueryBuilderUtil;
import org.iana.rzm.web.common.query.retriver.EntityRetriver;
import org.iana.rzm.web.tapestry.components.list.ImpactedPartiesListRequest;
import org.iana.rzm.web.tapestry.components.list.ListRequests;
import org.iana.rzm.web.tapestry.components.list.SortFactory;
import org.iana.rzm.web.user.services.UserServices;
import org.iana.web.tapestry.components.browser.Browser;
import org.iana.web.tapestry.components.browser.EntityQuery;
import org.iana.web.tapestry.components.browser.PaginatedEntity;
import org.iana.web.tapestry.components.browser.QueryException;


public abstract class UserRequestsPerspective extends UserPage
    implements PageBeginRenderListener, SortFactory, IExternalPage {

    public static final String PAGE_NAME = "UserRequestsPerspective";

    @Component(id = "listRequests", type = "rzmLib:ListRequests", bindings = {
        "entityQuery=prop:entityQuery",
        "listener=listener:viewRequestDetails",
        "linkTragetPage=prop:reviewDomainPage",
        "cancelRequestPage=literal:WithdrawRequest",
        "sortFactory=prop:sortFactory"
        })
    public abstract IComponent getListRequestComponent();

    @Component(id = "listImpactedpartRequests", type = "rzmLib:ImpactedPartiesListRequest", bindings = {
        "entityQuery=prop:entityQuery",
        "listener=listener:viewRequestDetails",
        "usePagination=literal:false",
        "noRecordsMessage=literal:'There are no outstanding requests."
        }
    )
    public abstract IComponent getListImpactedPartyRequestComponent();

    @Component(id = "back", type = "DirectLink", bindings = {
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER",
        "listener=listener:back"
        })
    public abstract IComponent getBackComponent();

    @Component(id = "useListReqiest", type = "If", bindings = {"condition=prop:useListRequest"})
    public abstract IComponent getUseListRequestComponent();

    @Component(id = "useImpactedParty", type = "Else")
    public abstract IComponent getUseImpactedPartyComponent();

    @Component(id = "showBack", type = "If", bindings = {"condition=prop:showBackLink", "element=literal:table"})
    public abstract IComponent getShowBackComponent();

    @Bean(PaginatedEntityQuery.class)
    public abstract PaginatedEntityQuery getPaginatedEntityBean();

    @InjectPage(RequestInformation.PAGE_NAME)
    public abstract RequestInformation getRequestInformation();

    @InjectPage(ReviewDomain.PAGE_NAME)
    public abstract ReviewDomain getReviewDomainPage();

    @Persist("client")
    public abstract EntityRetriver getEntityFetcher();

    public abstract void setEntityFetcher(EntityRetriver retriver);

    @Persist("client")
    public abstract void setSortField(SortOrder sortOrder);

    public abstract SortOrder getSortField();

    @Persist("client")
    public abstract ICallback getCallback();

    public abstract void setCallback(ICallback callback);

    @Persist("client")
    public abstract boolean isImpactedParty();

    public abstract void setImpactedParty(boolean b);

    public boolean isShowBackLink() {
        return getCallback() != null;
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length == 0 || parameters.length < 2) {
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
            return;
        }

        EntityRetriver entityRetriver = (EntityRetriver) parameters[0];
        SortOrder sortOrder = (SortOrder) parameters[1];
        setEntityFetcher(entityRetriver);
        setSortField(sortOrder);

        if (parameters.length > 2) {
            setImpactedParty(Boolean.valueOf(parameters[2].toString()));
        }

        if (parameters.length > 3) {
            setCallback((ICallback) parameters[3]);
        }

    }


    protected Object[] getExternalParameters() {
        return getCallback() == null ?
               new Object[]{getEntityFetcher(), getSortField(), isImpactedParty()} :
               new Object[]{getEntityFetcher(), getSortField(), isImpactedParty(), getCallback()};
    }

    public SortFactory getSortFactory() {
        return this;
    }

    public boolean isUseListRequest() {
        return !isImpactedParty();
    }

    public void pageBeginRender(PageEvent event) {
        if (getEntityFetcher() == null) {
            setEntityFetcher(new TransactionRetriver(getUserServices()));
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
        } catch (QueryException e) {
            getQueryExceptionHandler().handleQeruyException(e,GeneralError.PAGE_NAME);
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
        EntityRetriver entityRetriver = getEntityFetcher();
        entityRetriver.applySortOrder(getSortField());
        entityQuery.setFetcher(entityRetriver);
        return entityQuery;
    }

    public void viewRequestDetails(long requestId) {
        RequestInformation info = getRequestInformation();
        info.setImpactedThirdPartyView(isImpactedParty());
        info.setRequestId(requestId);
        info.setCallback(createCallback());
        getRequestCycle().activate(info);
    }

    public void back() {
        getCallback().performCallback(getRequestCycle());
    }

    protected Browser getBrowser() {
        if (isImpactedParty()) {
            return ((ImpactedPartiesListRequest) getComponent("listImpactedpartRequests")).getRecords();
        }
        return ((ListRequests) getComponent("listRequests")).getRecords();
    }

    private class TransactionRetriver implements EntityRetriver {
        private UserServices userServices;
        private Criterion criterion;
        private SortOrder sortOrder;

        public TransactionRetriver(UserServices userServices) {
            this.userServices = userServices;
            criterion = QueryBuilderUtil.empty();
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
