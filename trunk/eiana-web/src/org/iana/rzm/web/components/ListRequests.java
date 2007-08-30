package org.iana.rzm.web.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.tapestry.*;

@ComponentClass(allowBody = true)
public abstract class ListRequests extends ListRecords {

    @Component(id = "rt", type = "Insert", bindings = {"value=prop:record.rtId"})
    public abstract IComponent getRtComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:record.domainName"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "created", type = "Insert", bindings = {"value=prop:record.created"})
    public abstract IComponent getCreatedComponent();

    @Component(id = "state", type = "Insert", bindings = {"value= prop:record.currentStateAsString"})
    public abstract IComponent getStateComponent();

    @Component(id = "createdBy", type = "Insert", bindings = {"value=prop:record.createdBy"})
    public abstract IComponent getCreatedByComponent();

    @Component(id = "modified", type = "Insert", bindings = {"value=prop:record.modified"})
    public abstract IComponent getModifiedComponent();

    @Component(id="viewRequest", type="DirectLink",bindings = {
           "listener=prop:listener","parameters=prop:record.id",
               "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"
               })
       public abstract IComponent getListenerComponent();
    

    @Component(id="domainLink", type="DirectLink", bindings = {
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
        "listener=listener:goToDomain", "parameters=prop:record.domainName"
        })
    public abstract IComponent getDomainLinkComponent();

    @Parameter(required = false, defaultValue = "literal:View Details")
    public abstract String getActionTitle();

    @Parameter(required = true)
    public abstract LinkTraget getLinkTragetPage();

    public TransactionVOWrapper getRecord(){
        return (TransactionVOWrapper) getCurrentRecord();
    }

    public void goToDomain(String domainName){
        LinkTraget target = getLinkTragetPage();
        target.setIdentifier(domainName);
        getPage().getRequestCycle().activate(target);
    }
}