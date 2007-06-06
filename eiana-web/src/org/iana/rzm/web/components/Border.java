package org.iana.rzm.web.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.components.Insert;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.engine.ILink;
import org.apache.tapestry.html.Shell;
import org.iana.rzm.web.Global;
import org.iana.rzm.web.Visit;
import org.iana.rzm.web.pages.Login;
import org.iana.rzm.web.pages.MyPasswordChange;
import org.iana.rzm.web.services.ApplicationLifecycle;
import org.iana.rzm.web.tapestry.MessagePropertyCallback;

public abstract class Border extends BaseComponent {

    @Asset("css/master.css")
    public abstract IAsset getStylesheet();

    @Asset("js/rzm.js")
    public abstract IAsset getScript();

    @Asset("images/iana-logo.png")
    public abstract IAsset getBackground();

    @Component(type = "Shell", bindings = {
            "title=prop:windowTitle", "stylesheet=asset:stylesheet", "delegate=prop:javaScriptDelegator"})
    public abstract Shell getShell();

    @Component(type = "Insert", bindings = {"value=prop:userTitle"})
    public abstract Insert getLoggedInTitle();

    @Component(id = "loggedIn", type = "If",  bindings = {"condition=prop:loggedIn"})
    public abstract IComponent getloggedInCondition();

    @InjectObject("service:rzm.JavaScriptDelegator")
    public abstract IRender getJavaScriptDelegator();

    @InjectObject("service:rzm.ApplicationLifecycle")
    public abstract ApplicationLifecycle getApplicationLifecycle();

    @InjectObject("engine-service:home")
    public abstract IEngineService getPageService();


    @InjectState("visit")
    public abstract Visit getVisit();

    @InjectStateFlag("visit")
    public abstract boolean getVisitExists();

    @InjectState("global")
    public abstract Global getGlobal();

    @InjectPage("Login")
    public abstract Login getLogin();
     

    @InjectPage("MyPasswordChange")
    public abstract MyPasswordChange getPasswordChangePage();


    protected abstract MessagePropertyCallback getHomeCallback();

    public String getUserTitle() {
        return "Logged in as " + getVisit().getUser().getUserName();
    }

    public boolean isLoggedIn() {
        return getVisitExists() && getVisit().isUserLoggedIn();
    }

    public IPage logout() {
        getApplicationLifecycle().logout();
        return getLogin();
    }

    public MyPasswordChange changePassword() {
        MyPasswordChange myPasswordChange = getPasswordChangePage();
        myPasswordChange.setCallback(getHomeCallback());
        return myPasswordChange;
    }

    public ILink homePage() {
        return getPageService().getLink(true, null);
    }

    public boolean getIsDebugDisabled() {
        return !getGlobal().isDebugEnabled();
    }

}
