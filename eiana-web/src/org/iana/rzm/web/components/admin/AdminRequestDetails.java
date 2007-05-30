package org.iana.rzm.web.components.admin;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.components.RequestDetails;
import org.iana.rzm.web.pages.admin.EditRequest;
import org.iana.rzm.web.services.admin.AdminServices;

@ComponentClass
public abstract class AdminRequestDetails extends RequestDetails {

    @Component(id="requestSummery", type = "RequestSummery", bindings = {
            "domainName=prop:domainName", "listener=listener:editRequest","request=prop:request"
    })
    public abstract IComponent getRequestSummaryComponent();

    @Asset(value = "WEB-INF/admin/AdminRequestDetails.html")
    public abstract IAsset get$template();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getUserServices();

    @InjectPage("admin/EditRequest")
    public abstract EditRequest getEditRequest();

    protected AdminServices getRzmServices(){
        return getUserServices();
    }

    public void editRequest(){
        EditRequest editRequest = getEditRequest();
        editRequest.setRequestId(getRequestId());
        getPage().getRequestCycle().activate(editRequest);
    }
}
