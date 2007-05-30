package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.components.Browser;
import org.iana.rzm.web.components.ListRequests;
import org.iana.rzm.web.model.EntityFetcher;
import org.iana.rzm.web.model.EntityFetcherUtil;
import org.iana.rzm.web.model.EntityQuery;
import org.iana.rzm.web.model.PaginatedEntity;
import org.iana.rzm.web.services.OpenRequestFetcher;
import org.iana.rzm.web.services.PaginatedEntityQuery;
import org.iana.rzm.web.services.admin.AdminServices;

public abstract class AdminHome extends AdminPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "admin/AdminHome";

    @Component(id = "activeRequest", type = "SelectionLink", bindings = {"spanStyle=prop:activeSpanStyle",
            "linkStyle=prop:activeStyle", "linkText=literal:Show Active Only", "listener=listener:showActive", "useDivStyle=literal:true"})
    public abstract IComponent getActiveRequestComponent();

    @Component(id = "allRequest", type = "SelectionLink", bindings = {"spanStyle=prop:allSpanStyle",
            "linkStyle=prop:allStyle", "linkText=literal:Show All", "listener=listener:showAll"})
    public abstract IComponent getAllRequestComponent();

    @Component(id = "listRequests", type = "ListRequests", bindings = {
            "entityQuery=prop:entityQuery",
            "usePagination=literal:true",
            "noRequestMsg=literal:'There are no requests.'",
            "listener=listener:viewRequestDetails",
            "actionTitle=literal:Review / Edit"
            }
    )
    public abstract IComponent getListRequestComponent();


    @Bean(PaginatedEntityQuery.class)
    public abstract PaginatedEntityQuery getPaginatedEntityQuery();

    @InjectPage("admin/RequestInformation")
    public abstract RequestInformation getRequestInformation();

    @Persist("client:page")
    @InitialValue("false")
    public abstract boolean isShowAll();

    public abstract void setShowAll(boolean value);


    public void pageBeginRender(PageEvent event) {
        try {
            int count = getEntityQuery().getResultCount();
            Browser browser = ((ListRequests) getComponent("listRequests")).getRecords();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e);
        }
    }

    public void viewRequestDetails(long id){
        RequestInformation information = getRequestInformation();
        information.setRequestId(id);
        getRequestCycle().activate(information);
    }

    public String getBrowserTitle() {
        if (isShowAll()) {
            return "All Requests";
        }

        return "In-Process Requests";
    }


    public String getActiveSpanStyle() {
        if (isShowAll()) {
            return "leftGrey";
        }

        return "leftBlack";
    }

    public String getActiveStyle() {
        if (isShowAll()) {
            return "buttonGrey";
        }

        return "buttonBlack";
    }

    public String getAllSpanStyle() {
        if (isShowAll()) {
            return "leftBlack";
        }
        return "leftGrey";

    }

    public String getAllStyle() {
        if (isShowAll()) {
            return "buttonBlack";
        }
        return "buttonGrey";
    }

    public EntityQuery getEntityQuery() {
        PaginatedEntityQuery entityQuery = getPaginatedEntityQuery();
        entityQuery.setFetcher(getFetcher());
        return entityQuery;
    }

    public void showAll() {
        setShowAll(true);
    }

    public void showActive() {
        setShowAll(false);
    }

    private EntityFetcher getFetcher() {
        if (isShowAll()) {
            return new AllRequestFetcher(getAdminServices());
        }
        return new OpenRequestFetcher(getAdminServices());
    }

    private  class AllRequestFetcher implements EntityFetcher {

        private AdminServices services;
        private EntityFetcherUtil entityFetcherUtil;

        public AllRequestFetcher(AdminServices services) {
            this.services = services;
            entityFetcherUtil = new EntityFetcherUtil(this);
        }

        public int getTotal() throws NoObjectFoundException {
            return services.getTotalTransactionCount();
        }

        public PaginatedEntity[] getEntities() throws NoObjectFoundException {
            return services.getTransactions().toArray(new PaginatedEntity[0]);
        }

        public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
            return entityFetcherUtil.calculatePageResult(offset, length);
        }
    }

}
