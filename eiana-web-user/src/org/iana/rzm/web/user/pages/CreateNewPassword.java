package org.iana.rzm.web.user.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.iana.rzm.web.common.pages.BaseLogin;
import org.iana.rzm.web.common.pages.RzmPage;
import org.iana.rzm.web.common.services.ExternalPageErrorHandler;
import org.iana.rzm.web.common.services.RzmAuthenticationService;
import org.iana.rzm.web.tapestry.components.password.CreateNewPasswordEditor;
import org.iana.rzm.web.tapestry.components.password.PasswordEditor;

public abstract class CreateNewPassword extends RzmPage implements PasswordEditor, IExternalPage {

    public static final String PAGE_NAME = "CreateNewPassword";

    @Component(id = "passwordEditor",
               type = "rzmLib:CreateNewPasswordEditor",
               bindings = {"passwordEditor=prop:passwordEditor"})
    public abstract IComponent getPasswordEditorComponent();

    @InjectObject("service:rzm.RzmAuthenticationService")
    public abstract RzmAuthenticationService getAuthenticationService();

    @InjectObject("service:rzm.ExternalPageErrorHandler")
    public abstract ExternalPageErrorHandler getExternalPageErrorHandler();

    @InjectPage(Login.PAGE_NAME)
    public abstract Login getLogin();

    public BaseLogin getLoginPage(){
        return getLogin();       
    }

    public PasswordEditor getPasswordEditor() {
        return this;
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters == null || parameters.length < 2) {
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
            return;
        }

        String userName = (String) parameters[0];
        CreateNewPasswordEditor editor = (CreateNewPasswordEditor) getComponent("passwordEditor");
        editor.setUserName(userName);
        editor.setToken(parameters[1].toString());
    }

}
