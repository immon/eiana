package org.iana.rzm.web.components.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.components.*;
import org.iana.rzm.web.pages.user.*;
import org.iana.rzm.web.services.*;
import org.iana.rzm.web.services.user.*;
import org.iana.rzm.web.tapestry.*;

@ComponentClass
public abstract class UserWithdrawRequestConfirmation extends WithdrawRequestConfirmation {

    @Asset(value = "WEB-INF/WithdrawRequestConfirmation.html")
    public abstract IAsset get$template();
    
    @InjectObject("service:rzm.UserServices")
    public abstract UserServices getUserServices();

    @InjectPage(ReviewDomain.PAGE_NAME)
    public abstract ReviewDomain getReviewDomain();

    @InjectPage(UserHome.PAGE_NAME)
    public abstract UserHome getHome();

    public LinkTraget getDomainPage(){
        return getReviewDomain();
    }

    protected  RzmServices getRzmServices(){
        return getUserServices();
    }

    protected  String getExceptionPage(){
        return UserGeneralError.PAGE_NAME;
    }

    public MessagePropertyCallback getCallback(){
        return new MessagePropertyCallback(getHome());
    }
}
