package org.iana.rzm.web.admin.components;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.admin.pages.EditDomain;
import org.iana.rzm.web.admin.pages.GeneralError;
import org.iana.rzm.web.admin.pages.Home;
import org.iana.rzm.web.admin.services.AdminServices;
import org.iana.rzm.web.common.LinkTraget;
import org.iana.rzm.web.common.components.BaseWithdrawRequestConfirmation;
import org.iana.rzm.web.common.services.RzmServices;
import org.iana.web.tapestry.callback.MessagePropertyCallback;

@ComponentClass
public abstract class WithdrawRequestConfirmation extends BaseWithdrawRequestConfirmation {

    @Component(id = "requestSummery", type = "RequestSummery", bindings = {
        "domainName=prop:domainName", "request=prop:request", "linkTragetPage=prop:domainPage"
        })
    public abstract IComponent getRequestSummaryComponent();

    @Asset(value = "WEB-INF/admin/WithdrawRequestConfirmation.html")
    public abstract IAsset get$template();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getAdminServices();

    @InjectPage(EditDomain.PAGE_NAME)
    public abstract EditDomain getEditDomain();

    @InjectPage(Home.PAGE_NAME)
    public abstract Home getHome();

    public LinkTraget getDomainPage(){
        return getEditDomain();
    }

    protected RzmServices getRzmServices(){
        return getAdminServices();
    }

    protected  String getExceptionPage(){
        return GeneralError.PAGE_NAME;
    }

    public MessagePropertyCallback getCallback(){
        return new MessagePropertyCallback(getHome());
    }
}
