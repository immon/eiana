package org.iana.rzm.web.admin.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.admin.pages.*;
import org.iana.rzm.web.admin.services.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.components.*;
import org.iana.rzm.web.common.services.*;
import org.iana.web.tapestry.callback.*;

@ComponentClass
public abstract class WithdrawRequestConfirmation extends BaseWithdrawRequestConfirmation {

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
