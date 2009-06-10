package org.iana.rzm.web.admin.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.valid.*;
import org.iana.rzm.web.admin.pages.*;
import org.iana.rzm.web.admin.validators.*;
import org.iana.rzm.web.common.callback.*;
import org.iana.rzm.web.common.components.*;
import org.iana.rzm.web.common.pages.*;
import org.iana.rzm.web.common.query.finder.*;
import org.iana.web.tapestry.callback.*;
import org.iana.web.tapestry.feedback.*;
import org.iana.web.tapestry.validation.*;


public abstract class Border extends BaseBorder {

    public static final String WINDOW_TITLE = "IANA Root Zone Maintenance - Admin Interface ";

    @Component(id = "loggedIn1", type = "If",  bindings = {"condition=prop:loggedIn"})
    public abstract IComponent getloggedIn1Component();

    @Component(id = "navigation", type = "Navigation", bindings = {"selected=prop:selected"})
    public abstract IComponent getNavigationComponent();

    @Component(id = "searchLabel", type = "Insert", bindings = {"value=prop:searchLabel"})
    public abstract IComponent getSearchLabelComponent();


    @Component(id = "searchForm", type = "Form", bindings = {
        "clientValidationEnabled=literal:true",
        "delegate=prop:validationDelegate"
        })
    public abstract IComponent getSearchFormComponent();

    @Component(id = "searchText", type = "TextField", bindings = {"value=prop:search", "validators=validators:required"})
    public abstract IComponent getSearchTextComponent();

    @Component(id = "search", type = "LinkSubmit", bindings = {"listener=listener:doFind"})
    public abstract IComponent getSearchComponent();

    @InjectComponent("searchText")
    public abstract IFormComponent getSearchComponentField();

    @Asset(value = "WEB-INF/admin/Border.html")
    public abstract IAsset get$template();

    @InjectPage(Home.PAGE_NAME)
    public abstract MessageProperty getHome();

    @Parameter(required = false, defaultValue = "literal:REQUESTS")
    public abstract String getSelected();

    @Parameter(required = false)
    public abstract Finder getFinderListener();

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
        getVisit().resetAllModifiedDomain();
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

    public boolean isBackEnabled(){
        AdminPage page = (AdminPage) getPage();
        return page.getCallback() != null;
    }
}
