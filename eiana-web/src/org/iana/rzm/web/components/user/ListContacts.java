package org.iana.rzm.web.components.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.pages.*;
import org.iana.rzm.web.services.*;

import java.util.*;

@ComponentClass(allowBody = true)
public abstract class ListContacts extends BaseComponent {

    @Component(id = "list", type = "For", bindings = {"source=prop:contacts", "value=prop:contact"})
    public abstract IComponent getContactsComponent();

    @Component(id = "contact", type = "Contact", bindings = {
        "type=prop:type",
        "domainId=prop:domainId",
        "contactAttributes=prop:contact.map",
        "listener=prop:action",
        "editible=prop:editible",
        "rzmServices=prop:rzmServices",
        "errorPage=prop:errorPage"})
    public abstract IComponent getContactComponent();

    @Asset(value = "WEB-INF/user/ListContacts.html")
    public abstract IAsset get$template();

    @Parameter(required = true)
    public abstract String getType();

    @Parameter(required = true)
    public abstract List<ContactVOWrapper> getContacts();

    @Parameter(required = true)
    public abstract long getDomainId();

    @Parameter(required = true)
    public abstract IActionListener getAction();

    @Parameter(required = true)
    public abstract GeneralError getErrorPage();

    @Parameter(required = true)
    public abstract RzmServices getRzmServices();


    @Parameter(required = false, defaultValue = "true")
    public abstract boolean isEditible();

    public abstract ContactVOWrapper getContact();

}