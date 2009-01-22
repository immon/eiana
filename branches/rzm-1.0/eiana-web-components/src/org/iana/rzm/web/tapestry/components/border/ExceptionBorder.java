package org.iana.rzm.web.tapestry.components.border;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.components.*;
import org.apache.tapestry.html.*;
import org.iana.rzm.web.common.pages.*;
import org.iana.web.tapestry.callback.*;
import org.iana.web.tapestry.feedback.*;
import org.iana.web.tapestry.shell.*;

public abstract class ExceptionBorder extends BaseComponent {

    @Component(type = "Shell", bindings = {
        "title=prop:windowTitle", "stylesheet=asset:stylesheet", "delegate=prop:borderDeligator"})
    public abstract Shell getShell();

    @Component(id = "backgroundImage", type = "Image", bindings = {"image=prop:background", "style=prop:backgroundStyle"})
    public abstract IComponent getbackgroundImageComponent();

    @Component(type = "Insert", bindings = {"value=prop:userTitle"})
    public abstract Insert getLoggedInTitle();

    @Component(id = "loggedIn", type = "If",  bindings = {"condition=prop:loggedIn"})
    public abstract IComponent getloggedInCondition();

    @Asset("iana-logo-pageheader.png")
    public abstract IAsset getSiteLogo();

    @Asset("border.css")
    public abstract IAsset getStylesheet();

    @Asset("favicon.ico")
    public abstract IAsset getSiteIcon();

    public abstract MessageProperty getHomePage();
    public abstract MessageProperty getLoginPage();
    public abstract String getWindowTitle();

    public FavoriteIconDeligator getBorderDeligator(){
        return new FavoriteIconDeligator(getSiteIcon());
    }

    public RzmPage getRzmPage(){
        if(RzmPage.class.isAssignableFrom(getPage().getClass())){
            return (RzmPage)getPage();
        }

        return null;
    }

    public String getUserTitle() {

        Class aClass = getRzmPage() == null ? getPage().getClass() : getRzmPage().getClass();
        if(ProtectedPage.class.isAssignableFrom(aClass)){
            ProtectedPage page = (ProtectedPage) getRzmPage();
            return "Logged in as " + page.getVisitState().getUser().getUserName();
        }

        return "Not Login";
    }

    public String getBackgroundStyle() {
        if (isLoginPage()) {
            return "margin: 45px 0 0 0;";
        }
        return "margin: 15px 0 0 0;";
    }

    public IAsset getBackground() {
        return getSiteLogo();
    }

    private boolean isLoginPage() {
        String name = getPage().getPageName();
        return name.equals("Login");
    }

    public void homePage() {
        MessagePropertyCallback callback = getHomeCallback();
        callback.performCallback(getPage().getRequestCycle());
    }

    public IPage logout() {
        return isProtectedPage() ? getProtectedPage().logout() : null ;
    }


    protected MessageProperty getHome() {
        if (isLoggedIn()) {
            return getHomePage();
        }
        return getLoginPage();
    }

    protected MessagePropertyCallback getHomeCallback() {
        return new MessagePropertyCallback(getHome());
    }

    public boolean isLoggedIn() {
        return isRzmPage() && isProtectedPage() && getProtectedPage().getVisitState().isUserLoggedIn();
    }

    public boolean isAdmin(){
        return isProtectedPage() && getProtectedPage().getVisitState().getUser().isAdmin();
    }

    private ProtectedPage getProtectedPage() {
        return (ProtectedPage) getRzmPage();
    }

    private boolean isRzmPage() {
        return RzmPage.class.isAssignableFrom(getPage().getClass());
    }

    public boolean isProtectedPage(){
        if(getRzmPage() == null){
            return false;
        }
        
        Class aClass = getRzmPage().getClass();
        return ProtectedPage.class.isAssignableFrom(aClass);
    }
}
