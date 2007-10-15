package org.iana.rzm.web.components.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.pages.admin.*;

@ComponentClass
public abstract class Navigation extends BaseComponent {

    public static final String REQUEST = "REQUESTS";
    public static final String DOMAINS = "DOMAINS";
    public static final String USERS = "USERS";

    @Component(id="requestsLink", type="SelectionLink", bindings = {"spanStyle=prop:requestsSpanStyle",
            "linkStyle=prop:requestsStyle", "linkText=literal:Requests", "listener=listener:viewRequests", "useDivStyle=literal:true"} )
    public abstract IComponent getRequestsLinkComponent();

    @Component(id="domainsLink", type="SelectionLink", bindings = {"spanStyle=prop:domainsSpanStyle",
            "linkStyle=prop:domainsStyle", "linkText=literal:Domains", "listener=listener:viewDomains", "useDivStyle=literal:true"} )
    public abstract IComponent getDomainsLinkComponent();

    @Component(id="usersLink", type="SelectionLink", bindings = {"spanStyle=prop:usersSpanStyle",
            "linkStyle=prop:usersStyle", "linkText=literal:Users", "listener=listener:viewUsers", "useDivStyle=literal:true"} )
    public abstract IComponent getUsersLinkComponent();

    @Component(id="logout", type="DirectLink", bindings = {"listener=listener:logout",
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getDirectLinkComponent();

    @Parameter(required = false, defaultValue = "literal:REQUESTS")
    public abstract String getSelected();

    @Asset(value = "WEB-INF/admin/Navigation.html")
    public abstract IAsset get$template();

    @InjectPage("admin/Domains")
    public abstract Domains getDomains();

    @InjectPage("admin/AdminHome")
    public abstract AdminHome getHome();

    public String getRequestsStyle() {
        return getStyle(REQUEST);
    }

    public String getDomainsStyle() {
        return getStyle(DOMAINS);
    }

    public String getUsersStyle() {
        return getStyle(USERS);
    }


    public String getRequestsSpanStyle(){
        return getRequestsStyle().equals("buttonBlack") ? "leftBlack" : "leftGrey";
    }

    public String getDomainsSpanStyle(){
        return getDomainsStyle().equals("buttonBlack") ? "leftBlack" : "leftGrey";
    }

    public String getUsersSpanStyle(){
        return getUsersStyle().equals("buttonBlack") ? "leftBlack" : "leftGrey";
    }

    private String getStyle(String page) {
        if (getSelected().equals(page)){
            return "buttonBlack";
        }

        return "buttonGrey";
    }

    public IPage logout(){
        Border border = (Border) getPage().getComponent("border");
        return border.logout();
    }


    public void viewDomains(){
        AdminPage page = (AdminPage) getPage();
        page.resetStateIfneeded();

        if(!isSamePage(Domains.PAGE_NAME)){
            getPage().getRequestCycle().activate(Domains.PAGE_NAME);
        }
    }

    public void viewRequests(){
        if(!isSamePage(AdminHome.PAGE_NAME)){
            getPage().getRequestCycle().activate(AdminHome.PAGE_NAME);
        }
    }

    public void viewUsers(){
        if(!isSamePage(Users.PAGE_NAME)){
            getPage().getRequestCycle().activate(Users.PAGE_NAME);
        }

    }

    private boolean isSamePage(String pageName) {
        return getPage().getPageName().equals(pageName);
    }

}
