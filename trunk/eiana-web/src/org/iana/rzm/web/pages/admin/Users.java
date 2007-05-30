package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.web.components.Browser;
import org.iana.rzm.web.components.admin.ListUsers;
import org.iana.rzm.web.model.EntityFetcher;
import org.iana.rzm.web.model.EntityQuery;
import org.iana.rzm.web.model.PaginatedEntity;
import org.iana.rzm.web.model.UserVOWrapper;
import org.iana.rzm.web.services.PaginatedEntityQuery;
import org.iana.rzm.web.services.admin.AdminServices;

import java.util.List;

public abstract class Users extends AdminPage implements PageBeginRenderListener {

    public static final String PAGE_NAME = "admin/Users";

    @Component(id = "listUsers", type = "ListUsers", bindings = {
            "entityQuery=prop:entityQuery",
            "usePagination=literal:true",
            "noRequestMsg=literal:There are no users.",
            "listener=listener:viewUser"
            }
    )
    public abstract IComponent getListUsersComponent();


    public void pageBeginRender(PageEvent event) {

        try {
            int count = getEntityQuery().getResultCount();
            Browser browser = ((ListUsers) getComponent("listUsers")).getRecords();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }
        } catch (NoObjectFoundException e) {
            getObjectNotFoundHandler().handleObjectNotFound(e);
        }
    }

    public EntityQuery getEntityQuery() {
        PaginatedEntityQuery entityQuery = new PaginatedEntityQuery();
        entityQuery.setFetcher(new UsersFetcher(getAdminServices()));
        return entityQuery;
    }

    private static class UsersFetcher implements EntityFetcher {
        private AdminServices adminServices;


        public UsersFetcher(AdminServices adminServices) {
            this.adminServices = adminServices;
        }

        public int getTotal() throws NoObjectFoundException {
            return adminServices.getTotalUserCount();
        }

        public PaginatedEntity[] getEntities() throws NoObjectFoundException {
            List<UserVOWrapper> users = adminServices.getUsers();
            return users.toArray(new PaginatedEntity[0]);
        }

        public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
            return adminServices.getUsers(offset, length).toArray(new PaginatedEntity[0]);
        }
    }
}
