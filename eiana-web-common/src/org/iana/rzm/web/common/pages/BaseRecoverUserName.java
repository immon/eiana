package org.iana.rzm.web.common.pages;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectComponent;
import org.apache.tapestry.annotations.InjectObject;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.valid.IValidationDelegate;
import org.apache.tapestry.valid.ValidationConstraint;
import org.iana.rzm.facade.common.NoObjectFoundException;
import org.iana.rzm.facade.passwd.NonUniqueDataToRecoverUserException;
import org.iana.rzm.web.common.services.RzmAuthenticationService;

public abstract class BaseRecoverUserName extends RzmPage {

    @Component(id = "form", type = "Form",
            bindings = {
                    "stateful=literal:false",
                    "delegate=prop:validationDelegate"
                    }
    )
    public abstract IComponent getFormComponent();

    @Component(id = "email", type = "TextField",
            bindings = {
                    "displayName=literal:Email:",
                    "value=prop:email",
                    "validators=validators: email"
                    }
    )
    public abstract IComponent getUserNameComponent();

    @Component(id = "password", type = "TextField",
            bindings = {
                    "displayName=literal:Password:",
                    "value=prop:password",
                    "hidden=literal:true"
                    }
    )
    public abstract IComponent getPasswordComponent();

    @InjectComponent("email")
    public abstract IFormComponent getEmailField();

    @InjectComponent("password")
    public abstract IFormComponent getPasswordField();

    @InjectObject("service:rzm.RzmAuthenticationService")
    public abstract RzmAuthenticationService getAuthenticationService();

    @InjectPage(BaseLogin.PAGE_NAME)
    public abstract BaseLogin getLogin();

    public abstract String getPassword();

    public abstract String getEmail();

    public void recoverUserName() {
        IValidationDelegate validationDelegate = getValidationDelegate();

        String email = getEmail();
        String password = getPassword();

        if (StringUtils.isEmpty(email)) {
            validationDelegate.setFormComponent(getEmailField());
            validationDelegate.record("Please Specify your Email", ValidationConstraint.REQUIRED);
        }

        if (StringUtils.isEmpty(password)) {
            validationDelegate.setFormComponent(getPasswordField());
            validationDelegate.record("Please Specify your Password", ValidationConstraint.REQUIRED);
        }

        if (validationDelegate.getHasErrors()) {
            return;
        }

        try {
            String userName = getAuthenticationService().recoverUser(getEmail(), getPassword());
            BaseLogin login = getLogin();
            login.setInfoMessage("Your user name is " + userName);
            getRequestCycle().activate(login);
        } catch (NonUniqueDataToRecoverUserException e) {
            setErrorMessage(getMessageUtil().getRecoverUserNameMessage());
        } catch (NoObjectFoundException e) {
            setErrorMessage(getMessageUtil().getRecoverUserNameMessage());
        }
    }

    public void cancel() {
        BaseLogin login = getLogin();
        getRequestCycle().activate(login);
    }
}
