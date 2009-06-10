package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.admin.components.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.web.tapestry.components.browser.*;

public abstract class UsersPerspective extends AdminPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "UsersPerspective";

    @Component(id = "listUsers", type = "ListUsers", bindings = {
            "entityQuery=prop:entityQuery",
            "usePagination=literal:true",
            "noRequestMsg=literal:There are no users.",
            "listener=listener:editUser",
            "deleteListener=listener:deleteUser"
            }
    )                                                         
    public abstract IComponent getListUsersComponent();

    @InjectPage(EditSystemUser.PAGE_NAME)
    public abstract EditSystemUser getEditSystemUserPage();

     @InjectPage(EditAdminUser.PAGE_NAME)
    public abstract EditAdminUser getEditAdminUserPage();

    @Persist("client")
    public abstract EntityRetriver getEntityFetcher();
    public abstract void setEntityFetcher(EntityRetriver entityRetriver);

    public void pageBeginRender(PageEvent event) {

        try {
            int count = getEntityQuery().getResultCount();
            Browser browser = ((ListUsers) getComponent("listUsers")).getRecords();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        } catch (QueryException e) {
            getQueryExceptionHandler().handleQeruyException(e, GeneralError.PAGE_NAME);
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
