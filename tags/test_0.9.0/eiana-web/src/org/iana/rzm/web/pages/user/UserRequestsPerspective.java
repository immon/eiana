package org.iana.rzm.web.pages.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
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


public abstract class UserRequestsPerspective extends UserPage implements PageBeginRenderListener, SortFactory {

    public static final String PAGE_NAME = "user/UserRequestsPerspective";

    @Component(id = "listRequests", type = "ListRequests", bindings = {
        "entityQuery=prop:entityQuery", "listener=listener:viewRequestDetails",
        "linkTragetPage=prop:reviewDomainPage",
        "cancelRequestPage=literal:user/WithdrawRequest",
        "sortFactory=prop:sortFactory"
        })
    public abstract IComponent getListRequestComponent();

    @Bean(PaginatedEntityQuery.class)
    public abstract PaginatedEntityQuery getPaginatedEntityBean();

    @InjectPage("user/RequestInformation")
    public abstract RequestInformation getRequestInformation();

    @InjectPage("user/ReviewDomain")
    public abstract ReviewDomain getReviewDomainPage();

    @Persist("client:page")
    public abstract EntityFetcher getEntityFetcher();
    public abstract void setEntityFetcher(EntityFetcher fetcher);

    @Persist("client:page")
    public abstract void setSortField(SortOrder sortOrder);
    public abstract SortOrder getSortField();

    public SortFactory getSortFactory(){
        return this;
    }

    public void pageBeginRender(PageEvent event) {
        if (getEntityFetcher() == null) {
            setEntityFetcher(new TransactionFetcher(getUserServices()));
        }

        if(getSortField() == null){
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
        info.setRequestId(requestId);
        getRequestCycle().activate(info);
    }

    protected Browser getBrowser() {
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
