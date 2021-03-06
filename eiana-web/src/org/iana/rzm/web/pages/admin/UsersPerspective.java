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

public abstract class UsersPerspective extends AdminPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "admin/Users";

    @Component(id = "listUsers", type = "ListUsers", bindings = {
            "entityQuery=prop:entityQuery",
            "usePagination=literal:true",
            "noRequestMsg=literal:There are no users.",
            "listener=listener:editUser",
            "deleteListener=listener:deleteUser"
            }
    )
    public abstract IComponent getListUsersComponent();

    @InjectPage("admin/EditSystemUser")
    public abstract EditSystemUser getEditSystemUserPage();

     @InjectPage("admin/EditAdminUser")
    public abstract EditAdminUser getEditAdminUserPage();

    @Persist("client:page")
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
        LinkTraget page = getPage(admin);
        page.setIdentifier(userId);
        getRequestCycle().activate(page);
    }

    public void deleteUser(long userId, boolean admin){
        getAdminServices().deleteUser(userId);
    }

    private LinkTraget getPage(boolean admin) {
        return admin ? getEditAdminUserPage() : getEditSystemUserPage();
    }

}
