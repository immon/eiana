package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.components.admin.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.*;

public abstract class UsersPerspective extends AdminPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "admin/Users";

    @Component(id = "listUsers", type = "ListUsers", bindings = {
            "entityQuery=prop:entityQuery",
            "usePagination=literal:true",
            "noRequestMsg=literal:There are no users.",
            "listener=listener:editUser"
            }
    )
    public abstract IComponent getListUsersComponent();

    @InjectPage("admin/EditSystemUser")
    public abstract EditSystemUser getEditSystemUserPage();

     @InjectPage("admin/EditAdminUser")
    public abstract EditAdminUser getEditAdminUserPage();

    public abstract EntityFetcher getEntityFetcher();

    public abstract void setEntityFetcher(EntityFetcher entityFetcher);

    public void pageBeginRender(PageEvent event) {

        try {
            int count = getEntityQuery().getResultCount();
            Browser browser = ((ListUsers) getComponent("listUsers")).getRecords();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e, AdminGeneralError.PAGE_NAME);
        }
    }

    public EntityQuery getEntityQuery() {
        PaginatedEntityQuery entityQuery = new PaginatedEntityQuery();
        entityQuery.setFetcher(getEntityFetcher());
        return entityQuery;
    }

    public void editUser(long userId, boolean admin) {
        EntityIdPage page = getPage(admin);
        page.setEntityId(userId);
        getRequestCycle().activate((IPage) page);
    }

    private EntityIdPage getPage(boolean admin) {
        return admin ? getEditAdminUserPage() : getEditSystemUserPage();
    }

}
