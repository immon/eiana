package org.iana.rzm.web.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.services.*;

public abstract class ResetPassword extends RzmPage implements MessageProperty {

    @Component(id = "form", type = "Form",
            bindings = {
                    "success=listener:restorePassword",
                    "stateful=literal:false",
                    "clientValidationEnabled=literal:true"
                    }
    )
    public abstract IComponent getFormComponent();

    @Component(id = "username", type = "TextField",
            bindings = {
                    "displayName=literal:User name:",
                    "value=prop:userName",
                    "validators=validators:required"
                    }
    )
    public abstract IComponent getUserNameComponent();

    @InjectObject("service:rzm.RzmAuthenticationService")
    public abstract RzmAuthenticationService getAuthenticationService();

    @InjectPage("Login")
    public abstract Login getLogin();

    public abstract String getUserName();

    public void restorePassword(){
        Login login = getLogin();
        login.setInfoMessage("We have sent an email with a new password to the email address specified in your account");
        getRequestCycle().activate(login);
    }

}
