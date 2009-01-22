package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.criteria.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.admin.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.model.criteria.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.services.admin.*;
import org.iana.rzm.web.tapestry.*;

import java.util.*;

public abstract class AdminHome extends AdminPage implements PageBeginRenderListener, Search, SortFactory {

    public static final String PAGE_NAME = "admin/AdminHome";

    @Component(id = "activeRequest", type = "SelectionLink", bindings = {"spanStyle=prop:activeSpanStyle",
            "linkStyle=prop:activeStyle", "linkText=literal:Show Active Only", "listener=listener:showActive", "useDivStyle=literal:true"})
    public abstract IComponent getActiveRequestComponent();

    @Component(id = "allRequest", type = "SelectionLink", bindings = {"spanStyle=prop:allSpanStyle",
            "linkStyle=prop:allStyle", "linkText=literal:Show All", "listener=listener:showAll"})
    public abstract IComponent getAllRequestComponent();

    @Component(id = "createNew", type = "DirectLink", bindings = { "listener=listener:createNew",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCreateNewComponent();

    @Component(id = "listRequests", type = "ListRequests", bindings = {
            "entityQuery=prop:entityQuery",
            "usePagination=literal:true",
            "noRequestMsg=literal:'There are no requests.'",
            "listener=listener:viewRequestDetails",
            "actionTitle=literal:Review / Edit",
            "linkTragetPage=prop:editDomain",
            "cancelRequestPage=literal:admin/WithdrawRequest" ,
            "sortFactory=prop:sortFactory"
            }
    )
    public abstract IComponent getListRequestComponent();

    @InjectPage("admin/EditDomain")
    public abstract EditDomain getEditDomain();

    @InjectPage("admin/RequestsPerspective")
    public abstract RequestsPerspective getRequestsPerspective();

    @Bean(PaginatedEntityQuery.class)
    public abstract PaginatedEntityQuery getPaginatedEntityQuery();

    @InjectPage("admin/RequestInformation")
    public abstract RequestInformation getRequestInformation();

    @InjectPage("admin/DomainSelection")
    public abstract DomainSelection getDomainSelection();

    @Persist("client:app")
    public abstract boolean isShowAll();
    public abstract void setShowAll(boolean value);

    @Persist("client:app")
    public abstract SortOrder getSortField();
    public abstract void setSortField(SortOrder sort);

    public SortFactory getSortFactory(){
        return this;
    }

    public FinderListener getFinderListener(){
        return new RequestsFinderListener(getAdminServices(), getRequestCycle(), this, getRequestsPerspective());
    }

    public FinderValidator getFinderValidator(){
        return new RequestFinderValidator();
    }

    public void pageBeginRender(PageEvent event) {
        try {

            if(getSortField() == null){
                setSortField(new SortOrder());
            }

            int count = getEntityQuery().getResultCount();
            Browser browser = ((ListRequests) getComponent("listRequests")).getRecords();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        }catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }catch(AccessDeniedException e){
            getAccessDeniedHandler().handleAccessDenied(e, AdminGeneralError.PAGE_NAME);
        }
    }

    public void viewRequestDetails(long id){
        RequestInformation information = getRequestInformation();
        information.setRequestId(id);
        getRequestCycle().activate(information);
    }

    public void createNew(){
        DomainSelection domainSelection = getDomainSelection();
        domainSelection.resetStateIfneeded();
        domainSelection.setCallback(new RzmCallback(PAGE_NAME, getLogedInUserId()));
        getRequestCycle().activate(domainSelection);
    }

    public void sort(String field, boolean accending){
        setSortField(new SortOrder(field,accending));
    }

    public boolean isFieldSortable(String name){
        return true;
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
        SortOrder sortOrder = getSortField();
        if (isShowAll()) {
            return new AllRequestFetcher(getAdminServices(), sortOrder);
        }
        return new OpenRequestFetcher(getAdminServices(), sortOrder);
    }

    private static  class AllRequestFetcher implements EntityFetcher {

        private AdminServices services;
        private SortOrder sort;
        private Criterion criterion;

        public AllRequestFetcher(AdminServices services, SortOrder sort) {
            this.services = services;
            this.sort = sort;
            criterion = CriteriaBuilder.empty();
        }

        public int getTotal() throws NoObjectFoundException {
            return services.getTransactionCount(criterion);
        }

        public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
            List<TransactionVOWrapper> list = services.getTransactions(criterion, offset, length, sort);
            return list.toArray(new PaginatedEntity[0]);
        }

        public void applySortOrder(SortOrder sortOrder) {
            this.sort = sortOrder;
        }
    }

}
