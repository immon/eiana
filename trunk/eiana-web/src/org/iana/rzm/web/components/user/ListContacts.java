package org.iana.rzm.web.components.user;

import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IActionListener;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.Parameter;
import org.iana.rzm.web.model.ContactVOWrapper;

import java.util.List;

@ComponentClass(allowBody = true)
public abstract class ListContacts extends BaseComponent {

    @Component(id="list", type="For", bindings = {"source=prop:contacts","value=prop:contact"})
    public abstract IComponent getContactsComponent();

    @Component(id="contact", type="Contact", bindings = {
        "type=prop:type","domainId=prop:domainId","contactAttributes=prop:contact.map","listener=prop:action"})
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

    public abstract ContactVOWrapper getContact();

}