package org.iana.rzm.web.components.admin;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.ComponentClass;
import org.iana.rzm.web.components.ListRecords;
import org.iana.rzm.web.model.UserVOWrapper;

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
        "listener=prop:listener","parameters=prop:record.id",
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getListenerComponent();

    public UserVOWrapper getRecord() {
        return (UserVOWrapper) getCurrentRecord();
    }
    
}
