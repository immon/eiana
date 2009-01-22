package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.iana.rzm.web.common.*;

public abstract class WithdrawRequest extends AdminPage implements IExternalPage, LinkTraget {

    public static final String PAGE_NAME = "WithdrawRequest";

    @Component(id = "cancelRequest", type = "WithdrawRequestConfirmation", bindings = {"requestId=prop:requestId"})
    public abstract IComponent getRequestDetailsComponent();

    @Persist("client")
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
