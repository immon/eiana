package org.iana.rzm.web.common.pages;

import org.apache.commons.codec.digest.*;
import org.apache.commons.lang.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.engine.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.valid.*;
import org.apache.tapestry.web.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.web.common.services.*;
import org.iana.rzm.web.common.utils.*;
import org.iana.web.tapestry.feedback.*;
import org.iana.web.tapestry.link.*;

import java.rmi.server.*;

public abstract class BaseResetPassword extends RzmPage implements MessageProperty {

    @Component(id = "form", type = "Form",
               bindings = {
                   "stateful=literal:false",
                   "delegate=prop:validationDelegate"
                   }
    )
    public abstract IComponent getFormComponent();

    @Component(id = "username", type = "TextField",
               bindings = {
                   "displayName=literal:User name:",
                   "value=prop:userName"
                   }
    )
    public abstract IComponent getUserNameComponent();

    @InjectComponent("username")
    public abstract IFormComponent getUserNameField();

    @InjectObject("service:rzm.RzmAuthenticationService")
    public abstract RzmAuthenticationService getAuthenticationService();

    @InjectObject("service:tapestry.globals.WebRequest")
    public abstract WebRequest getWebRequest();

    @InjectObject("engine-service:external")
    public abstract IEngineService getExternalPageService();

    @InjectPage(BaseLogin.PAGE_NAME)
    public abstract BaseLogin getLogin();

    public abstract String getUserName();

    public void restorePassword() {
        IValidationDelegate validationDelegate = getValidationDelegate();
        String userName = getUserName();

        if (StringUtils.isEmpty(userName)) {
            validationDelegate.setFormComponent(getUserNameField());
            validationDelegate.record("Please Specify your User name", ValidationConstraint.REQUIRED);
        }

        if (validationDelegate.getHasErrors()) {
            return;
        }

        String token = DigestUtils.md5Hex(new UID().toString());
        ExternalServiceParameter parameter = new ExternalServiceParameter(getCreateNewPasswordPageName(), new Object[]{getUserName(), token});
        ILink link = getExternalPageService().getLink(true, parameter);
        String slink =
            LinkGenerator.encodeLink(link.getAbsoluteURL(null,
                                                         null,
                                                         WebUtil.getServerPort(getWebRequest().getServerPort()),
                                                         null,
                                                         true));
        try {
            getAuthenticationService().resetPassword(getUserName(), slink, token);
        } catch (PasswordChangeException e) {
            validationDelegate.setFormComponent(getUserNameField());
            validationDelegate.record("Use name " + e.getUserName() + " Does not exist", ValidationConstraint.REQUIRED);
            return;
        }
        BaseLogin login = getLogin();
        login.setInfoMessage("We have sent an email to the email address in your account with instructions on how to create a new password");
        getRequestCycle().activate(login);
    }

    protected abstract String getCreateNewPasswordPageName();

    public void cancel() {
        BaseLogin login = getLogin();
        getRequestCycle().activate(login);
    }

}
