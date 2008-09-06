package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.rzm.web.tapestry.components.list.*;
import org.iana.web.tapestry.components.browser.*;


public abstract class RequestsPerspective extends AdminPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "RequestsPerspective";

    @Component(id = "listRequests", type = "rzmLib:ListRequests", bindings = {
            "entityQuery=prop:entityQuery", "listener=listener:viewRequestDetails",
            "linkTragetPage=prop:editDomain",
            "cancelRequestPage=literal:admin/WithdrawRequest"
        })
    public abstract IComponent getListRequestComponent();

    @Bean(PaginatedEntityQuery.class)
    public abstract PaginatedEntityQuery getPaginatedEntityBean();

    @InjectPage(RequestInformation.PAGE_NAME)
    public abstract RequestInformation getRequestInformation();

    @InjectPage(EditDomain.PAGE_NAME)
    public abstract EditDomain getEditDomain();

    @Persist("client")
    public abstract ICallback getCallback();
    public abstract void setCallback(ICallback callback);


    public abstract EntityRetriver getEntityFetcher();
    public abstract void setEntityFetcher(EntityRetriver retriver);

    public void pageBeginRender(PageEvent event) {
        try {
            int count = getEntityQuery().getResultCount();
            Browser browser = getBrowser();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        } catch (QueryException e) {
            getQueryExceptionHandler().handleQeruyException(e, GeneralError.PAGE_NAME);
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
