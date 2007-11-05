package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.*;


public abstract class RequestsPerspective extends AdminPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "admin/RequestsPerspective";

    @Component(id = "listRequests", type = "ListRequests", bindings = {
            "entityQuery=prop:entityQuery", "listener=listener:viewRequestDetails",
            "linkTragetPage=prop:editDomain",
            "cancelRequestPage=literal:admin/WithdrawRequest"
        })
    public abstract IComponent getListRequestComponent();

    @Bean(PaginatedEntityQuery.class)
    public abstract PaginatedEntityQuery getPaginatedEntityBean();

    @InjectPage("admin/RequestInformation")
    public abstract RequestInformation getRequestInformation();

    @InjectPage("admin/EditDomain")
    public abstract EditDomain getEditDomain();

    public abstract EntityFetcher getEntityFetcher();

    public abstract void setEntityFetcher(EntityFetcher fetcher);

    public void pageBeginRender(PageEvent event) {
        try {
            int count = getEntityQuery().getResultCount();
            Browser browser = getBrowser();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        } catch (AccessDeniedException e) {
            getAccessDeniedHandler().handleAccessDenied(e, AdminGeneralError.PAGE_NAME);
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
}
