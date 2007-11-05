package org.iana.rzm.web.pages.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.criteria.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.services.user.*;


public abstract class UserRequestsPerspective extends UserPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "user/UserRequestsPerspective";

    @Component(id = "listRequests", type = "ListRequests", bindings = {
            "entityQuery=prop:entityQuery", "listener=listener:viewRequestDetails",
             "linkTragetPage=prop:reviewDomainPage",
             "cancelRequestPage=literal:user/WithdrawRequest"
        })
    public abstract IComponent getListRequestComponent();

    @Bean(PaginatedEntityQuery.class)
    public abstract PaginatedEntityQuery getPaginatedEntityBean();

    @InjectPage("user/RequestInformation")
    public abstract RequestInformation getRequestInformation();

    @InjectPage("user/ReviewDomain")
    public abstract ReviewDomain getReviewDomainPage();

    public abstract EntityFetcher getEntityFetcher();

    public abstract void setEntityFetcher(EntityFetcher fetcher);

    public void pageBeginRender(PageEvent event) {
        if (getEntityFetcher() == null) {
            setEntityFetcher(new TransactionFetcher(getUserServices()));
        }

        try {
            int count = getEntityQuery().getResultCount();
            Browser browser = getBrowser();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, UserGeneralError.PAGE_NAME);
        }catch(AccessDeniedException e){
            getAccessDeniedHandler().handleAccessDenied(e, UserGeneralError.PAGE_NAME);
        }
    }

    public EntityQuery getEntityQuery() {
        PaginatedEntityQuery entityQuery = getPaginatedEntityBean();
        entityQuery.setFetcher(getEntityFetcher());
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

    private  class TransactionFetcher implements EntityFetcher {
        private UserServices userServices;
        private Criterion criterion;

        public TransactionFetcher(UserServices userServices) {
            this.userServices = userServices;
            criterion = CriteriaBuilder.empty();
        }


        public int getTotal() {
            return userServices.getTransactionCount(criterion);
        }

        public PaginatedEntity[] get(int offset, int length)throws NoObjectFoundException {
            return userServices.getTransactions(criterion, offset, length).toArray(new PaginatedEntity[0]);
        }
    }


}
