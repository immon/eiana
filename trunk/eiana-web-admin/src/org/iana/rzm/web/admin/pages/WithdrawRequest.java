package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.callback.ICallback;
import org.iana.rzm.web.common.LinkTraget;

public abstract class WithdrawRequest extends AdminPage implements IExternalPage, LinkTraget {

    public static final String PAGE_NAME = "WithdrawRequest";

    @Component(id = "cancelRequest", type = "WithdrawRequestConfirmation", bindings = {"requestId=prop:requestId"})
    public abstract IComponent getRequestDetailsComponent();

    @Persist("client")
    public abstract void setRequestId(long requestId);
    public abstract long getRequestId();

    public abstract void setCallback(ICallback callback);

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
