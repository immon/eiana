package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.components.admin.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.tapestry.*;

public abstract class PollMessagesPerspective extends AdminPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "admin/PollMessagesPerspective";


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
        "listener=listener:back", "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getBackComponent();

    @Bean(PaginatedEntityQuery.class)
    public abstract PaginatedEntityQuery getPaginatedEntityBean();

    @InjectPage(ViewPollMessage.PAGE_NAME)
    public abstract ViewPollMessage getViewPollMessage();

    @Persist()
    public abstract RzmCallback getCallback();
    public abstract void setCallback(RzmCallback callback);

    //public FinderValidator getFinderValidator() {
    //    return new DomainFinderValidator();
    //}

    //public FinderListener getFinderListener() {
    //    return new DomainsFinderListener(getAdminServices(), getRequestCycle(), this, this);
    //}



    @Persist
    public abstract void setEntityFetcher(EntityFetcher entityFetcher);
    public abstract EntityFetcher getEntityFetcher();

    public void pageBeginRender(PageEvent event) {
        try {
            int count = getEntityQuery().getResultCount();
            Browser browser = ((ListPollMessages) getComponent("listMessages")).getRecords();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
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
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
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
