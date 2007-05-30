package org.iana.rzm.web.pages.user;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Bean;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.components.Browser;
import org.iana.rzm.web.components.ListRequests;
import org.iana.rzm.web.model.EntityFetcher;
import org.iana.rzm.web.model.EntityFetcherUtil;
import org.iana.rzm.web.model.EntityQuery;
import org.iana.rzm.web.model.PaginatedEntity;
import org.iana.rzm.web.services.PaginatedEntityQuery;
import org.iana.rzm.web.services.user.UserServices;


public abstract class UserRequestsPerspective extends UserPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "user/UserRequestsPerspective";

    @Component(id = "listRequests", type = "ListRequests", bindings = {
            "entityQuery=prop:entityQuery", "listener=listener:viewRequestDetails"})
    public abstract IComponent getListRequestComponent();

    @Bean(PaginatedEntityQuery.class)
    public abstract PaginatedEntityQuery getPaginatedEntityBean();

    @InjectPage("user/RequestInformation")
    public abstract RequestInformation getRequestInformation();

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
            getObjectNotFoundHandler().handleObjectNotFound(e);
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
        private EntityFetcherUtil entityFetcherUtil;

        public TransactionFetcher(UserServices userServices) {
            this.userServices = userServices;
            entityFetcherUtil = new EntityFetcherUtil(TransactionFetcher.this);
        }


        public int getTotal() {
            return userServices.getTotalTransactionCount();
        }

        public PaginatedEntity[] getEntities() throws NoObjectFoundException {
            return userServices.getTransactions().toArray(new PaginatedEntity[0]);
        }

        public PaginatedEntity[] get(int offset, int length)throws NoObjectFoundException {
            return entityFetcherUtil.calculatePageResult(offset, length);
        }
    }


}
