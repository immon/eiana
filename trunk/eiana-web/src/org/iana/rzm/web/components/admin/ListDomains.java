package org.iana.rzm.web.components.admin;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.ComponentClass;
import org.iana.rzm.web.components.ListRecords;
import org.iana.rzm.web.model.DomainVOWrapper;

@ComponentClass(allowBody = true)
public abstract class ListDomains extends ListRecords{

    @Asset( value = "WEB-INF/admin/ListDomains.html")
    public abstract IAsset get$template();

    @Asset(value = "images/spacer.png")
    public abstract IAsset getSpacer();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:record.name"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "modified", type = "Insert", bindings = {
        "value=prop:record.modified" })
    public abstract IComponent getModifiedComponent();

    @Component(id="viewDomain", type="DirectLink",bindings = {
        "listener=prop:listener","parameters=prop:record.id",
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getListenerComponent();

    public DomainVOWrapper getRecord() {
        return (DomainVOWrapper) getCurrentRecord();
    }
}