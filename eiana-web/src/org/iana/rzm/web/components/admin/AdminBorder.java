package org.iana.rzm.web.components.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.valid.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.admin.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.pages.*;
import org.iana.rzm.web.pages.admin.*;
import org.iana.rzm.web.tapestry.*;


public abstract class AdminBorder extends Border {

    public static final String WINDOW_TITLE = "IANA Root Zone Maintenance";

    @Component(id = "navigation", type = "Navigation", bindings = {"selected=prop:selected"})
    public abstract IComponent getNavigationComponent();

    @Component(id = "searchLabel", type = "Insert", bindings = {"value=prop:searchLabel"})
    public abstract IComponent getSearchLabelComponent();


    @Component(id = "searchForm", type = "Form", bindings = {
        "clientValidationEnabled=literal:true",
        "delegate=prop:validationDelegate"
        })
    public abstract IComponent getSearchFormComponent();

    @Component(id = "searchText",
               type = "TextField",
               bindings = {"value=prop:search", "validators=validators:required"})
    public abstract IComponent getSearchTextComponent();

    @Component(id = "search", type = "LinkSubmit", bindings = {"listener=listener:doFind"})
    public abstract IComponent getSearchComponent();

    @InjectComponent("searchText")
    public abstract IFormComponent getSearchComponentField();

    @Asset(value = "WEB-INF/admin/AdminBorder.html")
    public abstract IAsset get$template();

    @InjectPage("admin/AdminHome")
    public abstract MessageProperty getHome();

    @Parameter(required = false, defaultValue = "literal:REQUESTS")
    public abstract String getSelected();

    @Parameter(required = false)
    public abstract FinderListener getFinderListener();

    @Parameter(required = false)
    public abstract FinderValidator getFinderValidator();

    @Parameter(required = false)
    public abstract IRender getPageScriptDeligator();

    @InjectPage(AdminPasswordChange.PAGE_NAME)
    public abstract AdminPasswordChange getPasswordChangePage();

    public abstract String getSearch();


    protected IRender javaScriptDelegator() {
        if(getPageScriptDeligator() == null){
            return super.javaScriptDelegator();
        }

        return getPageScriptDeligator();
    }

    public Object[]getWhoIsParameters(){
        AdminPage adminPage = (AdminPage) getPage();
        return new Object[]{adminPage.getAdminServices()};
    }
    
    public void changePassword() {
        AdminPasswordChange myPasswordChange = getPasswordChangePage();
        AdminPage current = (AdminPage) getPage();
        RzmCallback callback = current.createCallback();
        myPasswordChange.setCallBack(callback);
        getPage().getRequestCycle().activate(myPasswordChange);
    }


    public boolean isSearchActive() {
        return Search.class.isAssignableFrom(getPage().getClass());
    }

    public IValidationDelegate getValidationDelegate() {
        RzmPage page = (RzmPage) getPage();
        IanaValidationDelegate delegate = (IanaValidationDelegate) page.getValidationDelegate();
        delegate.setDeligateErrorFieldClass("bordererrorMessageField");
        return delegate;
    }

    public void doFind() {
        getVisit().resetModifirdDomain();
        try {
            getFinderValidator().validate(getSearch());
            getFinderListener().doFind(getSearch());
        } catch (FinderValidationException e) {
            RzmPage page = (RzmPage) getPage();
            page.setErrorField(getSearchComponentField(), e.getMessage());
        }
    }

    public String getSearchLabel() {
        String type =
            getSelected();
        if (type.equals("REQUESTS")) {
            return "Request:";
        } else if (type.equals("USERS")) {
            return "User:";
        } else {
            return "Domain:";
        }
    }

    protected MessagePropertyCallback getHomeCallback() {
        return new MessagePropertyCallback(getHome());
    }

    public String getWindowTitle() {
        return WINDOW_TITLE;
    }
}
