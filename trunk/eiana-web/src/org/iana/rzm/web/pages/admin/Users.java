package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.admin.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.components.admin.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.services.admin.*;

public abstract class Users extends AdminPage implements PageBeginRenderListener, Search {

    public static final String PAGE_NAME = "admin/Users";

    @Component(id = "systemUsers", type = "SelectionLink", bindings = {"spanStyle=prop:activeSpanStyle",
              "linkStyle=prop:activeStyle", "linkText=literal:System Users", "listener=listener:showSystemUsers", "useDivStyle=literal:true"})
      public abstract IComponent getSystemUsersComponent();

      @Component(id = "adminUsers", type = "SelectionLink", bindings = {"spanStyle=prop:allSpanStyle",
              "linkStyle=prop:allStyle", "linkText=literal:Admin Users", "listener=listener:showAdminUsers"})
      public abstract IComponent getAllRequestComponent();


    @Component(id = "listUsers", type = "ListUsers", bindings = {
            "entityQuery=prop:entityQuery",
            "usePagination=literal:true",
            "noRequestMsg=literal:There are no users.",
            "listener=listener:editUser"
            }
    )
    public abstract IComponent getListUsersComponent();

    @Component(id = "newuser", type = "DirectLink", bindings = {"listener=listener:createUser",
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getnewuserComponent();


    @InjectPage("admin/CreateSystemUser")
    public abstract CreateSystemUser getCreateSystemUserPage();

    @InjectPage("admin/EditSystemUser")
    public abstract EditSystemUser getEditSystemUserPage();

    @InjectPage("admin/CreateAdminUser")
    public abstract CreateAdminUser getCreateAdminUserPage();

    @InjectPage("admin/EditAdminUser")
    public abstract EditAdminUser getEditAdminUserPage();


    @InjectPage("admin/UsersPerspective")
    public abstract UsersPerspective getUsersPerspective();

    @Persist("client:app")
    public abstract boolean isAdminUsers();
    public abstract void setAdminUsers(boolean value);

    public FinderValidator getFinderValidator(){
        return new EmptyFinderValidator();
    }

    public FinderListener getFinderListener(){
        return new UsersFinderListener(getAdminServices(),getRequestCycle(),this, getUsersPerspective());
    }

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

    public void showAdminUsers(){
        setAdminUsers(true);
    }

    public void showSystemUsers(){
        setAdminUsers(false);
    }

    public String getBrowserTitle() {
        if (isAdminUsers()) {
            return "Admin Users";
        }

        return "System  Users";
    }

    public String getActiveSpanStyle() {
        if (isAdminUsers()) {
            return "leftGrey";
        }

        return "leftBlack";
    }

    public String getActiveStyle() {
        if (isAdminUsers()) {
            return "buttonGrey";
        }

        return "buttonBlack";
    }

    public String getAllSpanStyle() {
        if (isAdminUsers()) {
            return "leftBlack";
        }
        return "leftGrey";

    }

    public String getAllStyle() {
        if (isAdminUsers()) {
            return "buttonBlack";
        }
        return "buttonGrey";
    }

    public EntityQuery getEntityQuery() {
        PaginatedEntityQuery entityQuery = new PaginatedEntityQuery();
        entityQuery.setFetcher(getFetcher());
        return entityQuery;
    }

    private EntityFetcher getFetcher() {
        if (isAdminUsers()) {
            return new AdminUsersFetcher(getAdminServices());
        }
        return new SystemUsersFetcher(getAdminServices());
    }

    public void editUser(long userId, boolean admin) {
        EntityIdPage page = getPage(admin);
        page.setEntityId(userId);
        getRequestCycle().activate((IPage) page);
    }

    private EntityIdPage getPage(boolean admin) {
        return admin ? getEditAdminUserPage() : getEditSystemUserPage();
    }

    public void createUser(){
        if(isAdminUsers()){
            createAdminUser();
        }else{
            createSystemUser();
        }
    }

    public void createSystemUser() {
        getRequestCycle().activate(getCreateSystemUserPage());
    }
    
    public void createAdminUser(){
         getRequestCycle().activate(getCreateAdminUserPage());
    }

    private static class AdminUsersFetcher implements EntityFetcher {
        private AdminServices adminServices;
        private Criterion criterion;


        public AdminUsersFetcher(AdminServices adminServices) {
            this.adminServices = adminServices;
            criterion = CriteriaBuilder.adminUsers();
        }

        public int getTotal() throws NoObjectFoundException {
            return adminServices.getUserCount(criterion);
        }

        public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
            return adminServices.getUsers(criterion, offset, length).toArray(new PaginatedEntity[0]);
        }
    }

    private static class SystemUsersFetcher implements EntityFetcher {
        private AdminServices adminServices;
        private Criterion criterion;

        public SystemUsersFetcher(AdminServices adminServices) {
            this.adminServices = adminServices;
            criterion = CriteriaBuilder.systemUsers();
        }

        public int getTotal() throws NoObjectFoundException {
            return adminServices.getUserCount(criterion);
        }

        public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
            return adminServices.getUsers(criterion, offset, length).toArray(new PaginatedEntity[0]);
        }
    }
}
