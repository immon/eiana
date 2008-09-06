package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.apache.tapestry.form.*;
import org.iana.criteria.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.web.admin.components.*;
import org.iana.rzm.web.admin.query.*;
import org.iana.rzm.web.admin.query.finders.*;
import org.iana.rzm.web.admin.query.retriver.*;
import org.iana.rzm.web.admin.services.*;
import org.iana.rzm.web.admin.validators.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.criteria.*;
import org.iana.rzm.web.common.query.*;
import org.iana.rzm.web.common.query.finder.*;
import org.iana.rzm.web.common.query.retriver.*;
import org.iana.web.tapestry.components.browser.*;

import java.io.*;
import java.util.*;

public abstract class Users extends AdminPage implements PageBeginRenderListener, Search {

    public static final String PAGE_NAME = "Users";

    @Component(id = "systemUsers", type = "SelectionLink", bindings = {"spanStyle=prop:systemSpanStyle",
        "linkStyle=prop:systemStyle", "linkText=literal:System Users", "listener=listener:showSystemUsers", "useDivStyle=literal:true"})
    public abstract IComponent getSystemUsersComponent();

    @Component(id = "adminUsers", type = "SelectionLink", bindings = {"spanStyle=prop:adminSpanStyle",
        "linkStyle=prop:adminStyle", "linkText=literal:Admin Users", "listener=listener:showAdminUsers", "useDivStyle=literal:true"})
    public abstract IComponent getAdminUsersComponent();

    @Component(id = "docUsers", type = "SelectionLink", bindings = {"spanStyle=prop:docSpanStyle",
        "linkStyle=prop:docStyle", "linkText=literal:DoC/Verisign", "listener=listener:showDocVerisignUsers"})
    public abstract IComponent getDoCComponent();


    @Component(id = "listUsers", type = "ListUsers", bindings = {
        "entityQuery=prop:entityQuery",
        "usePagination=literal:true",
        "noRequestMsg=literal:There are no users.",
        "listener=listener:editUser",
        "deleteListener=listener:deleteUser"
        }
    )
    public abstract IComponent getListUsersComponent();

    @Component(id = "createAdmin", type = "DirectLink", bindings = {"listener=listener:createAdminUser",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCreateAdminUserComponent();

    @Component(id = "createSystem", type = "DirectLink", bindings = {"listener=listener:createSystemUser",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCreateSystemUserComponent();

    @Component(id = "domainsList", type = "PropertySelection", bindings = {
        "model=prop:selectionModel", "value=prop:selectedDomain"
        })
    public abstract IComponent getCountriesComponent();

    @Component(id = "usersForDomain", type = "DirectLink", bindings = {"listener=listener:usersForDomain",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getViewUsersForDomainComponent();

    @Asset("js/users.js")
    public abstract IAsset getUsersScript();

    @InjectPage(CreateSystemUser.PAGE_NAME)
    public abstract CreateSystemUser getCreateSystemUserPage();

    @InjectPage(EditSystemUser.PAGE_NAME)
    public abstract EditSystemUser getEditSystemUserPage();

    @InjectPage(CreateAdminUser.PAGE_NAME)
    public abstract CreateAdminUser getCreateAdminUserPage();

    @InjectPage(EditAdminUser.PAGE_NAME)
    public abstract EditAdminUser getEditAdminUserPage();

    @InjectPage(UsersPerspective.PAGE_NAME)
    public abstract UsersPerspective getUsersPerspective();

    @Persist("client")
    public abstract boolean isAdminUsers();
     public abstract void setAdminUsers(boolean value);

    @Persist("client")
    public abstract boolean isSystemUsers();
    public abstract void setSystemUsers(boolean b);

    @Persist("client")
    public abstract boolean isDocVerisignUsers();
    public abstract void setDocVerisignUsers(boolean b);

    @Persist("client")
    public abstract List<String> getDomainNames();
    public abstract void setDomainNames(List<String> list);

    @Persist("client")
    public abstract String getSelectedDomain();

    public FinderValidator getFinderValidator() {
        return new EmptyFinderValidator();
    }

    public Finder getFinderListener() {
        return new UsersFinder(getAdminServices(), getRequestCycle(), this, getUsersPerspective(), getMessageUtil());
    }

    public IRender getPageScriptDeligator() {
        return new UsersScriptDeligator(getUsersScript());
    }


    public void pageBeginRender(PageEvent event) {

        if (!isAdminUsers() && (!isDocVerisignUsers())) {
            setSystemUsers(true);
        }

        try {
            int count = getEntityQuery().getResultCount();
            Browser browser = ((ListUsers) getComponent("listUsers")).getRecords();
            if (count != browser.getResultCount()) {
                browser.initializeForResultCount(count);
            }

            if (getDomainNames() == null) {
                setDomainNames(getAdminServices().getDomainNames());
            }

        } catch (QueryException e) {
            getQueryExceptionHandler().handleQeruyException(e, GeneralError.PAGE_NAME);
        }
    }

    public IPropertySelectionModel getSelectionModel() {
        return new DomainsModel(getDomainNames());
    }

    public void viewUsersForDomain(){
        String domain = getSelectedDomain();
        UsersPerspective perspective = getUsersPerspective();
        perspective.setEntityFetcher(new SearchUsersEntityRetriver(getAdminServices(), QueryBuilderUtil.usersForDomains(Arrays.asList(domain))));
    }


    public void showAdminUsers() {
        setAdminUsers(true);
        setSystemUsers(false);
        setDocVerisignUsers(false);
    }


    public void showSystemUsers() {
        setSystemUsers(true);
        setAdminUsers(false);
        setDocVerisignUsers(false);
    }


    public void showDocVerisignUsers() {
        setDocVerisignUsers(true);
        setAdminUsers(false);
        setSystemUsers(false);
    }


    public String getBrowserTitle() {
        if (isAdminUsers()) {
            return "Admin Users";
        } else if (isSystemUsers()) {
            return "System  Users";
        } else {
            return "DoC/Verisign Users";
        }
    }

    public String getSystemSpanStyle() {
        if (isAdminUsers() || isDocVerisignUsers()) {
            return "leftGrey";
        }

        return "leftBlack";
    }

    public String getSystemStyle() {
        if (isAdminUsers() || isDocVerisignUsers()) {
            return "buttonGrey";
        }

        return "buttonBlack";
    }

    public String getAdminSpanStyle() {
        if (isAdminUsers()) {
            return "leftBlack";
        }
        return "leftGrey";

    }

    public String getAdminStyle() {
        if (isAdminUsers()) {
            return "buttonBlack";
        }
        return "buttonGrey";
    }

    public String getDocStyle() {
        if (isDocVerisignUsers()) {
            return "buttonBlack";
        }
        return "buttonGrey";
    }

    public String getDocSpanStyle() {
        if (isDocVerisignUsers()) {
            return "leftBlack";
        }
        return "leftGrey";

    }

    public EntityQuery getEntityQuery() {
        PaginatedEntityQuery entityQuery = new PaginatedEntityQuery();
        entityQuery.setFetcher(getFetcher());
        return entityQuery;
    }

    private EntityRetriver getFetcher() {
        if (isAdminUsers()) {
            return new AdminUsersRetriver(getAdminServices());
        } else if (isDocVerisignUsers()) {
            return new DoCVerisignUsersRetriver(getAdminServices());
        }
        return new SystemUsersRetriver(getAdminServices());
    }

    public void editUser(long userId, boolean admin) {
        LinkTraget page = getPage(admin);
        page.setIdentifier(userId);
        getRequestCycle().activate(page);
    }

    private LinkTraget getPage(boolean admin) {
        return admin ? getEditAdminUserPage() : getEditSystemUserPage();
    }

    public void createSystemUser() {
        getRequestCycle().activate(getCreateSystemUserPage());
    }

    public void createAdminUser() {
        getRequestCycle().activate(getCreateAdminUserPage());
    }

    public void deleteUser(long userId, boolean admin) {
        getAdminServices().deleteUser(userId);
    }

    private static class AdminUsersRetriver implements EntityRetriver {
        private AdminServices adminServices;
        private Criterion criterion;


        public AdminUsersRetriver(AdminServices adminServices) {
            this.adminServices = adminServices;
            criterion = AdminQueryUtil.adminUsers();
        }

        public int getTotal() throws NoObjectFoundException {
            return adminServices.getUserCount(criterion);
        }

        public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
            return adminServices.getUsers(criterion, offset, length).toArray(new PaginatedEntity[0]);
        }

        public void applySortOrder(SortOrder sortOrder) {

        }
    }

    private static class SystemUsersRetriver implements EntityRetriver {
        private AdminServices adminServices;
        private Criterion criterion;

        public SystemUsersRetriver(AdminServices adminServices) {
            this.adminServices = adminServices;
            criterion = AdminQueryUtil.systemUsers();
        }

        public int getTotal() throws NoObjectFoundException {
            return adminServices.getUserCount(criterion);
        }

        public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
            return adminServices.getUsers(criterion, offset, length).toArray(new PaginatedEntity[0]);
        }

        public void applySortOrder(SortOrder sortOrder) {

        }

    }

    private static class DoCVerisignUsersRetriver implements EntityRetriver {
        private AdminServices adminServices;
        private Criterion criterion;

        public DoCVerisignUsersRetriver(AdminServices adminServices) {
            this.adminServices = adminServices;
            criterion = AdminQueryUtil.docVerisignUsers();
        }

        public int getTotal() throws NoObjectFoundException {
            return adminServices.getUserCount(criterion);
        }

        public PaginatedEntity[] get(int offset, int length) throws NoObjectFoundException {
            return adminServices.getUsers(criterion, offset, length).toArray(new PaginatedEntity[0]);
        }

        public void applySortOrder(SortOrder sortOrder) {
        }
    }

    public static class UsersScriptDeligator implements IRender {
        private IAsset javaScript;

        public UsersScriptDeligator(IAsset javaScript) {
            this.javaScript = javaScript;
        }

        public void render(IMarkupWriter writer, IRequestCycle cycle) {
            IComponent border = cycle.getPage().getComponent("border");
            writeScript(writer, border.getAsset("script"));
            writeScript(writer, javaScript);
        }

        private void writeScript(IMarkupWriter writer, IAsset asset) {
            writer.begin("script");
            writer.attribute("language", "javascript");
            writer.attribute("src", asset.buildURL());
            writer.end();
        }
    }

    private class DomainsModel implements IPropertySelectionModel, Serializable {
        private List<String> domains;

        public DomainsModel(List<String> domains) {
            this.domains = domains;
        }

        public int getOptionCount() {
            return domains.size();
        }

        public Object getOption(int index) {
            return domains.get(index);
        }

        public String getLabel(int index) {
            return domains.get(index);
        }

        public String getValue(int index) {
            return domains.get(index);
        }

        public Object translateValue(String value) {
            return value;
        }

    }
}



