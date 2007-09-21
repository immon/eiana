package org.iana.rzm.web.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.services.*;

public abstract class RecoverUserName extends RzmPage{
    @Component(id = "form", type = "Form",
            bindings = {
                    "success=listener:recoverUserName",
                    "stateful=literal:false",
                    "clientValidationEnabled=literal:true"
                    }
    )
    public abstract IComponent getFormComponent();

    @Component(id = "email", type = "TextField",
            bindings = {
                    "displayName=literal:Email:",
                    "value=prop:email",
                    "validators=validators:required, email"
                    }
    )
    public abstract IComponent getUserNameComponent();

   @Component(id = "password", type = "TextField",
            bindings = {
                    "displayName=literal:Password:",
                    "value=prop:password",
                    "validators=validators:required",
                    "hidden=literal:true"
                    }
    )
    public abstract IComponent getPasswordComponent();

    @InjectObject("service:rzm.RzmAuthenticationService")
    public abstract RzmAuthenticationService getAuthenticationService();

    @InjectPage("Login")
    public abstract Login getLogin();

    public abstract String getPassword();
    public abstract String getEmail();

    public void recoverUserName(){
        Login login = getLogin();
        login.setInfoMessage("We have sent an email with your user name to the email address specified in your account");
        getRequestCycle().activate(login);
    }
}
