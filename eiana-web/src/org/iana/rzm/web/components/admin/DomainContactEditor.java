package org.iana.rzm.web.components.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.pages.admin.*;
import org.iana.rzm.web.services.admin.*;

public abstract class DomainContactEditor extends BaseComponent {

    @Component(id = "isnew", type = "If", bindings = {"condition=prop:new"})
    public abstract IComponent getIsNewComponent();

     @Component(id = "isempty", type = "If", bindings = {"condition=prop:contactEmpty"})
    public abstract IComponent getIsEmptyComponent();

    @Component(id="title", type="Insert", bindings = {"value=prop:title"})
    public abstract IComponent getTitleComponent();

    @Component(
        id = "currentDetails", type = "Contact",
        bindings = {
            "type=prop:contactType",
            "domainId=prop:domainId",
            "originalAttributes=prop:originalContact.map",
            "contactAttributes=prop:contact.map",
            "listener=prop:contactListener",
            "editible=literal:true",
            "rzmServices=prop:rzmServices",
            "errorPage=prop:errorPage"
            }
    )
    public abstract IComponent getCurrentDetailsComponent();

    @Component(
        id = "newCurrentDetails", type = "Contact",
        bindings = {
            "type=prop:contactType",
            "domainId=prop:domainId",
            "originalAttributes=prop:originalContact.map",
            "contactAttributes=prop:contact.map",
            "listener=prop:contactListener",
            "editible=literal:true",
            "new=literal:true",
            "rzmServices=prop:rzmServices",
            "errorPage=prop:errorPage"
            }
    )
    public abstract IComponent getNewCurrentDetailsComponent();

    @Component(id = "new", type = "DirectLink", bindings = {
        "listener=prop:contactListener", "parameters=prop:contactType",
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getNewLinkComponent();

    @Component(id="noContactMessage", type="Insert", bindings = {"value=prop:message"})
    public abstract IComponent getNoContactMessageComponent();

    @Asset(value = "WEB-INF/admin/DomainContactEditor.html")
    public abstract IAsset get$template();

    @Parameter(required = true)
    public abstract IActionListener getContactListener();

    @Parameter(required = true)
    public abstract ContactVOWrapper getContact();

    @Parameter(required = true)
    public abstract ContactVOWrapper getOriginalContact();

    @Parameter(required = true)
    public abstract String getContactType();

    @Parameter(required = false, defaultValue = "0")
    public abstract long getDomainId();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getRzmServices();

    @InjectPage("admin/AdminGeneralError")
    public abstract AdminGeneralError getErrorPage();

    public boolean isNew() {
        return getContact().isNew();
    }

    public boolean isContactEmpty(){
        return getContact().isEmpty();
    }

    public String getMessage(){
        return getContactType() + " Is not assign";
    }

    public String getTitle(){
       return getContactType();        
    }
}
