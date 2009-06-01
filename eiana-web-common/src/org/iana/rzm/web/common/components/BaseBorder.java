package org.iana.rzm.web.common.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.components.Insert;
import org.apache.tapestry.engine.IEngineService;
import org.apache.tapestry.html.Shell;
import org.iana.rzm.web.common.Global;
import org.iana.rzm.web.common.Visit;
import org.iana.rzm.web.common.pages.BaseLogin;
import org.iana.web.tapestry.callback.MessagePropertyCallback;
import org.iana.web.tapestry.session.ApplicationLifecycle;

public abstract class BaseBorder extends BaseComponent {

    @Asset("css/master.css")
    public abstract IAsset getStylesheet();

    @Asset("js/rzm.js")
    public abstract IAsset getScript();

    @Asset("images/favicon.ico")
    public abstract IAsset getSiteIcon();
    
    @Asset("js/prototype.js")
    public abstract IAsset getPrototypeScript();

    @Asset("images/iana-logo-pageheader.png")
    public abstract IAsset getSiteLogo();

    @Component(id="backgroundImage", type="Image", bindings = {"image=prop:background", "style=prop:backgroundStyle"})
    public abstract IComponent getbackgroundImageComponent();

    @Component(type = "Shell", bindings = {"title=prop:windowTitle", "stylesheet=asset:stylesheet", "delegate=prop:javaScriptDelegator"})
    public abstract Shell getShell();

    @Component(type = "Insert", bindings = {"value=prop:userTitle"})
    public abstract Insert getLoggedInTitle();

    @Component(id = "loggedIn", type = "If",  bindings = {"condition=prop:loggedIn"})
    public abstract IComponent getloggedInCondition();

    @InjectObject("service:rzm.JavaScriptDelegator")
    public abstract IRender getDefaultJavaScriptDelegator();

    @InjectObject("service:rzm.ApplicationLifecycle")
    public abstract ApplicationLifecycle getApplicationLifecycle();

    @InjectObject("engine-service:home")
    public abstract IEngineService getPageService();

    public  IRender  getJavaScriptDelegator(){
        return javaScriptDelegator();   
    }

    protected IRender javaScriptDelegator(){
        return getDefaultJavaScriptDelegator();
    }

    @InjectState("visit")
    public abstract Visit getVisit();

    @InjectStateFlag("visit")
    public abstract boolean getVisitExists();

    @InjectState("global")
    public abstract Global getGlobal();

    @InjectPage(BaseLogin.PAGE_NAME)
    public abstract BaseLogin getLogin();

    protected abstract MessagePropertyCallback getHomeCallback();

    public String getBackgroundStyle(){
        if(isLoginPage()){
            return "margin: 45px 0 0 0;";
        }
        return "margin: 15px 0 0 0;";
    }

    public IAsset getBackground(){
        return getSiteLogo();
    }

    private boolean isLoginPage() {
        String name = getPage().getPageName();
        return name.equals("Login");
    }

    public String getUserTitle() {
        return "Logged in as " + getVisit().getUser().getUserName();
    }

    public boolean isLoggedIn() {
        return getVisitExists() && getVisit().isUserLoggedIn();
    }

    public boolean isRoot(){
        return isLoggedIn() && getVisit().getUser().isRoot();        
    }

    public IPage logout() {
        getApplicationLifecycle().logout();
        return getLogin();
    }

    public void homePage() {
        MessagePropertyCallback callback = getHomeCallback();
        callback.performCallback(getPage().getRequestCycle());
    }

    public boolean getIsDebugDisabled() {
        return !getGlobal().isDebugEnabled();
    }

}
