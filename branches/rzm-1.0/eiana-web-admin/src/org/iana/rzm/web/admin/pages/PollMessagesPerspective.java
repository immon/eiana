package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.admin.components.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.web.tapestry.components.browser.*;

public abstract class PollMessagesPerspective extends AdminPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "PollMessagesPerspective";


    @Component(id = "listMessages", type = "ListPollMessages", bindings = {
        "entityQuery=prop:entityQuery",
        "usePagination=literal:true",
        "noRequestMsg=literal:'There are no Poll messages.'",
        "listener=listener:viewMessage",
        "deleteListener=listener:deleteMessage"
        }
    )
    public abstract IComponent getListRequestComponent();

     @Component(id = "back", type = "DirectLink", bindings = {
        "listener=listener:back", "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getBackComponent();

    @Bean(PaginatedEntityQuery.class)
    public abstract PaginatedEntityQuery getPaginatedEntityBean();

    @InjectPage(ViewPollMessage.PAGE_NAME)
    public abstract ViewPollMessage getViewPollMessage();

    @Persist
    public abstract ICallback getCallback();
    public abstract void setCallback(ICallback callback);


    @Persist
    public abstract void setEntityFetcher(EntityRetriver entityRetriver);
    public abstract EntityRetriver getEntityFetcher();

    public void pageBeginRender(PageEvent event) {
        try {
            int count = getEntityQuery().getResultCount();
            Browser browser = ((ListPollMessages) getComponent("listMessages")).getRecords();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        } catch (QueryException e) {
            getQueryExceptionHandler().handleQeruyException(e, GeneralError.PAGE_NAME);
        }
    }

    public void viewMessage(long id) {
        ViewPollMessage page = getViewPollMessage();
        page.setEppMessageId(id);
        page.activate();
    }

    public void deleteMessage(long id){
        try {
            getAdminServices().deletePollMessage(id);
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, GeneralError.PAGE_NAME);
        }
    }

    public void back(){
        getCallback().performCallback(getRequestCycle());
    }

    public EntityQuery getEntityQuery() {
        PaginatedEntityQuery entityQuery = getPaginatedEntityBean();
        entityQuery.setFetcher(getEntityFetcher());
        return entityQuery;
    }
}
