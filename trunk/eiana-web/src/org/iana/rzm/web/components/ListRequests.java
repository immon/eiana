package org.iana.rzm.web.components;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.annotations.Parameter;
import org.iana.rzm.web.model.TransactionVOWrapper;

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

    @Parameter(required = false, defaultValue = "literal:View Details")
    public abstract String getActionTitle();


    public TransactionVOWrapper getRecord(){
        return (TransactionVOWrapper) getCurrentRecord();
    }

}