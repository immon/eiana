package org.iana.rzm.web.admin.components;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.IPage;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.ICallback;
import org.iana.rzm.web.admin.pages.*;
import org.iana.rzm.web.common.components.BaseBorder;

@ComponentClass
public abstract class Navigation extends BaseComponent {

    public static final String REQUEST = "REQUESTS";
    public static final String DOMAINS = "DOMAINS";
    public static final String USERS = "USERS";
    public static final String SYSTEM_SETTINGS = "SYSTEM_SETTINGS";
    public static final String EMAIL_TEMPLATE = "EMAIL_TEMPLATE";

    @Component(id = "requestsLink", type = "SelectionLink", bindings = {"spanStyle=prop:requestsSpanStyle",
        "linkStyle=prop:requestsStyle", "linkText=literal:Requests", "listener=listener:viewRequests", "useDivStyle=literal:true"})
    public abstract IComponent getRequestsLinkComponent();

    @Component(id = "domainsLink", type = "SelectionLink", bindings = {"spanStyle=prop:domainsSpanStyle",
        "linkStyle=prop:domainsStyle", "linkText=literal:Domains", "listener=listener:viewDomains", "useDivStyle=literal:true"})
    public abstract IComponent getDomainsLinkComponent();

    @Component(id = "usersLink", type = "SelectionLink", bindings = {"spanStyle=prop:usersSpanStyle",
        "linkStyle=prop:usersStyle", "linkText=literal:Users", "listener=listener:viewUsers", "useDivStyle=literal:true"})
    public abstract IComponent getUsersLinkComponent();

    @Component(id = "systemLink", type = "SelectionLink", bindings = {"spanStyle=prop:systemSpanStyle",
        "linkStyle=prop:systemStyle", "linkText=literal:System Settings", "listener=listener:viewSystemSettings", "useDivStyle=literal:true"})
    public abstract IComponent getSystemLinkComponent();

    @Component(id = "templates", type = "SelectionLink", bindings = {"spanStyle=prop:templatesSpanStyle",
        "linkStyle=prop:templatesStyle", "linkText=literal:System Templates", "listener=listener:viewTemplates", "useDivStyle=literal:true"})
    public abstract IComponent getTemplatesLinkComponent();

    @Component(id = "logout", type = "DirectLink", bindings = {"listener=listener:logout",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getLogoutComponent();

    @Component(id = "back", type = "DirectLink", bindings = {"listener=listener:back",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getBackComponent();


    @Component(id = "backOn", type = "If", bindings = {"condition=prop:backEnabled"})
    public abstract IComponent getBackOnComponent();

    @Component(id = "root", type = "If", bindings = {"condition=prop:root"})
    public abstract IComponent getRootComponent();


    @Parameter(required = false, defaultValue = "literal:REQUESTS")
    public abstract String getSelected();

    @Asset(value = "WEB-INF/admin/Navigation.html")
    public abstract IAsset get$template();

    @InjectPage(Domains.PAGE_NAME)
    public abstract Domains getDomains();

    @InjectPage(Home.PAGE_NAME)
    public abstract Home getHome();

    public String getRequestsStyle() {
        return getStyle(REQUEST);
    }

    public String getDomainsStyle() {
        return getStyle(DOMAINS);
    }

    public String getUsersStyle() {
        return getStyle(USERS);
    }

    public String getSystemStyle() {
        return getStyle(SYSTEM_SETTINGS);
    }

    public String getTemplatesStyle() {
        return getStyle(EMAIL_TEMPLATE);
    }

    public String getRequestsSpanStyle() {
        return getRequestsStyle().equals("buttonBlack") ? "leftBlack" : "leftGrey";
    }

    public String getDomainsSpanStyle() {
        return getDomainsStyle().equals("buttonBlack") ? "leftBlack" : "leftGrey";
    }

    public String getUsersSpanStyle() {
        return getUsersStyle().equals("buttonBlack") ? "leftBlack" : "leftGrey";
    }

    public String getSystemSpanStyle() {
        return getSystemStyle().equals("buttonBlack") ? "leftBlack" : "leftGrey";
    }

    public String getTemplatesSpanStyle() {
        return getTemplatesStyle().equals("buttonBlack") ? "leftBlack" : "leftGrey";
    }

    private String getStyle(String page) {
        if (getSelected().equals(page)) {
            return "buttonBlack";
        }

        return "buttonGrey";
    }

    public IPage logout() {
        BaseBorder border = getBorder();
        return border.logout();
    }

    private BaseBorder getBorder() {
        return (BaseBorder) getPage().getComponent("border");
    }

    public boolean isRoot(){
        return getBorder().isRoot();        
    }

    public void back() {
        AdminPage page = (AdminPage) getPage();
        ICallback callback = page.getCallback();
        callback.performCallback(page.getRequestCycle());
    }


    public void viewDomains() {
        AdminPage page = (AdminPage) getPage();
        page.resetStateIfneeded();

        if (!isSamePage(Domains.PAGE_NAME)) {
            getPage().getRequestCycle().activate(Domains.PAGE_NAME);
        }
    }

    public void viewRequests() {
        if (!isSamePage(Home.PAGE_NAME)) {
            getPage().getRequestCycle().activate(Home.PAGE_NAME);
        }
    }

    public void viewUsers() {
        if (!isSamePage(Users.PAGE_NAME)) {
            getPage().getRequestCycle().activate(Users.PAGE_NAME);
        }

    }

    public void viewSystemSettings() {
        if (!isSamePage(SystemSettings.PAGE_NAME)) {
            getPage().getRequestCycle().activate(SystemSettings.PAGE_NAME);
        }

    }

    public void viewTemplates() {
        if (!isSamePage(EmailTemplateSettings.PAGE_NAME)) {
            getPage().getRequestCycle().activate(EmailTemplateSettings.PAGE_NAME);
        }

    }

    public boolean isBackEnabled() {
        if (AdminPage.class.isAssignableFrom(getPage().getClass())){
            AdminPage page = (AdminPage) getPage();
            return page.getCallback() != null;
        }
        return false;
    }

    private boolean isSamePage(String pageName) {
        return getPage().getPageName().equals(pageName);
    }

}
