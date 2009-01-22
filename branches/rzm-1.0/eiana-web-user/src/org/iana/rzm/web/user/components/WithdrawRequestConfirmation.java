package org.iana.rzm.web.user.components;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.components.*;
import org.iana.rzm.web.common.services.*;
import org.iana.rzm.web.user.pages.*;
import org.iana.rzm.web.user.services.*;
import org.iana.web.tapestry.callback.*;

@ComponentClass
public abstract class WithdrawRequestConfirmation extends BaseWithdrawRequestConfirmation {

    @Component(id = "requestSummery", type = "RequestSummery", bindings = {
        "domainName=prop:domainName", "request=prop:request", "linkTragetPage=prop:domainPage"
        })
    public abstract IComponent getRequestSummaryComponent();

    @Asset(value = "WEB-INF/user/WithdrawRequestConfirmation.html")
    public abstract IAsset get$template();
    
    @InjectObject("service:rzm.UserServices")
    public abstract UserServices getUserServices();

    @InjectPage(ReviewDomain.PAGE_NAME)
    public abstract ReviewDomain getReviewDomain();

    @InjectPage(Home.PAGE_NAME)
    public abstract Home getHome();

    public LinkTraget getDomainPage(){
        return getReviewDomain();
    }

    protected RzmServices getRzmServices(){
        return getUserServices();
    }

    protected  String getExceptionPage(){
        return GeneralError.PAGE_NAME;
    }

    public MessagePropertyCallback getCallback(){
        return new MessagePropertyCallback(getHome());
    }
}
