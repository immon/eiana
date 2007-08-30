package org.iana.rzm.web.components.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.model.*;

@ComponentClass(allowBody = true)
public abstract class ListUsers extends ListRecords {

    @Asset( value = "WEB-INF/admin/ListUsers.html")
    public abstract IAsset get$template();

    @Asset(value = "images/spacer.png")
    public abstract IAsset getSpacer();

    @Component(id = "userName", type = "Insert", bindings = {"value=prop:record.userName"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "modified", type = "Insert", bindings = {
        "value=prop:record.modified" })
    public abstract IComponent getModifiedComponent();

    @Component(id="viewUser", type="DirectLink",bindings = {
        "listener=prop:listener","parameters=prop:listenerParameters",
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getListenerComponent();

    public UserVOWrapper getRecord() {
        return (UserVOWrapper) getCurrentRecord();
    }

    public Object[] getListenerParameters(){
        return new Object[]{getRecord().getId(), getRecord().isAdmin()};
    }
    
}
