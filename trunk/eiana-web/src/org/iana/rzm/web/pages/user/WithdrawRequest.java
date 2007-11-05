package org.iana.rzm.web.pages.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.tapestry.*;

public abstract class WithdrawRequest extends UserPage implements IExternalPage, LinkTraget {

    public static final String PAGE_NAME = "user/WithdrawRequest";

    @Component(id = "cancelRequest", type = "UserWithdrawRequestConfirmation", bindings = {"requestId=prop:requestId"})
    public abstract IComponent getRequestDetailsComponent();

      @Component(id = "overview", type = "PageLink", bindings = {"page=literal:user/UserHome",
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getHomeLinkComponent();

    @Persist("client:page")
    public abstract void setRequestId(long requestId);
    public abstract long getRequestId();

    public void setIdentifier(Object id){
        if(!Long.class.isAssignableFrom(id.getClass())){
            setRequestId(0);
        }else{
            setRequestId(((Long)id));
        }
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length == 0) {
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }
        setRequestId((Long) parameters[0]);
    }
   
    protected Object[] getExternalParameters() {
        return new Object[]{getRequestId()};
    }
}

