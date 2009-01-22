package org.iana.rzm.web.components.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.pages.admin.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.services.admin.*;
import org.iana.rzm.web.tapestry.*;

@ComponentClass
public abstract class AdminWithdrawRequestConfirmation extends WithdrawRequestConfirmation {

    @Asset(value = "WEB-INF/WithdrawRequestConfirmation.html")
    public abstract IAsset get$template();

    @InjectObject("service:rzm.AdminServices")
    public abstract AdminServices getAdminServices();

    @InjectPage(EditDomain.PAGE_NAME)
    public abstract EditDomain getEditDomain();

    @InjectPage(AdminHome.PAGE_NAME)
    public abstract AdminHome getHome();

    public LinkTraget getDomainPage(){
        return getEditDomain();
    }

    protected RzmServices getRzmServices(){
        return getAdminServices();
    }

    protected  String getExceptionPage(){
        return AdminGeneralError.PAGE_NAME;
    }

    public MessagePropertyCallback getCallback(){
        return new MessagePropertyCallback(getHome());
    }
}
