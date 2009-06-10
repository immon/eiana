package org.iana.rzm.web.admin.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.model.*;
import org.iana.web.tapestry.components.browser.*;

@ComponentClass(allowBody = true)
public abstract class ListDomains extends ListRecords {
                                  
    @Asset(value = "WEB-INF/admin/ListDomains.html")
    public abstract IAsset get$template();

    @Asset(value = "images/spacer.png")
    public abstract IAsset getSpacer();

    @Component(id = "pagination", type = "tapestry4Commons:BrowserPagination", bindings = {"browser=prop:records"})
    public abstract IComponent getPaginationComponent();

    @Component(id = "records", type = "tapestry4Commons:Browser", bindings = {
        "entityQuery=prop:entityQuery", "value=prop:currentRecord", "element=literal:tr"})
    public abstract IComponent getBrowserComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:record.name"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "description", type = "InsertText", bindings = {"value=prop:record.description"})
    public abstract IComponent getDescriptionComponent();

    @Component(id = "modified", type = "Insert", bindings = {
        "value=prop:record.modified"})
    public abstract IComponent getModifiedComponent();

    @Component(id = "checkboxImage", type = "Image", bindings = "image=prop:imageAsset")
    public abstract IComponent getCheckboxImageComponent();

    @Component(id = "viewDomain", type = "DirectLink", bindings = {
        "listener=prop:listener", "parameters=prop:record.id",
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getListenerComponent();

    @Asset("images/checkbox_on.png")
    public abstract IAsset getCheckboxOn();

    @Asset("images/checkbox_off.png")
    public abstract IAsset getCheckboxOff();


    public DomainVOWrapper getRecord() {
        return (DomainVOWrapper) getCurrentRecord();
    }

    public IAsset getImageAsset(){
        return getRecord().getSpecialInstructions() != null ? getCheckboxOn() :
               getCheckboxOff();
    }
}