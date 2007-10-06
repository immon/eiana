package org.iana.rzm.web.pages;

import org.apache.commons.lang.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.form.*;
import org.apache.tapestry.valid.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.web.services.*;

public abstract class RecoverUserName extends RzmPage {
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
        
    @InjectPage("Login")
    public abstract Login getLogin();

    public abstract String getPassword();

    public abstract String getEmail();

    public void recoverUserName() {
        IValidationDelegate validationDelegate = getValidationDelegate();

        String email = getEmail();
        String password = getPassword();

        if(StringUtils.isEmpty(email)){
            validationDelegate.setFormComponent(getEmailField());
            validationDelegate.record("Please Specify your Email", ValidationConstraint.REQUIRED);
        }

        if(StringUtils.isEmpty(password)){
            validationDelegate.setFormComponent(getPasswordField());
            validationDelegate.record("Please Specify your Password", ValidationConstraint.REQUIRED);
        }

        if(validationDelegate.getHasErrors()){
            return;
        }

        try {
            String userName = getAuthenticationService().recoverUser(getEmail(), getPassword());
            Login login = getLogin();
            login.setInfoMessage("Your user name is " + userName );
            getRequestCycle().activate(login);
        } catch (NonUniqueDataToRecoverUserException e) {
            setErrorMessage("We couldn't retreave your user name base on the information.\n Please contact IANA for help");
            return;
        } catch (NoObjectFoundException e) {
            setErrorMessage("We couldn't retreave your user name base on the information.\n Please contact IANA for help");
            return;
        }
    }

    public void cancel() {
        Login login = getLogin();
        getRequestCycle().activate(login);
    }
}
