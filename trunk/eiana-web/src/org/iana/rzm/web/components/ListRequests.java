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

    @Component(id="showCancelRequest", type="If", bindings = {"condition=prop:requestDeleteble", "element=literal:div"})
    public abstract IComponent getShowCancelRequestComponent();

    @Component(id="cancelRequest",type="DirectLink",bindings = {
                "listener=listener:cancelRequest", "parameters=prop:record.id",
               "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelRequestComponent();

    @Parameter(required = false, defaultValue = "literal:View Details")
    public abstract String getActionTitle();

    @Parameter(required = true)
    public abstract LinkTraget getLinkTragetPage();

    @Parameter(required = true)
    public abstract String getCancelRequestPage();

    public TransactionVOWrapper getRecord(){
        return (TransactionVOWrapper) getCurrentRecord();
    }

    public boolean isRequestDeleteble(){
        return ((TransactionVOWrapper) getCurrentRecord()).canCancel();
    }

    public void goToDomain(String domainName){
        LinkTraget target = getLinkTragetPage();
        target.setIdentifier(domainName);
        getPage().getRequestCycle().activate(target);
    }

    public void cancelRequest(long requestId){
        LinkTraget page = (LinkTraget) getPage().getRequestCycle().getPage(getCancelRequestPage());
        page.setIdentifier(requestId);
        getPage().getRequestCycle().activate(page);
    }
}