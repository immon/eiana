package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.form.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.web.common.callback.*;
import org.iana.rzm.web.common.listeners.*;

public abstract class AdminPasswordChange extends AdminPage implements MyChangePasswordListener {

    public static final String PAGE_NAME = "AdminPasswordChange";

    @Component(id="changePassword", type="rzmLib:MyPasswordChangeEditor", bindings = {"listener=prop:listener"})
    public abstract IComponent getChangePasswordEditorComponent();

    @Component(id="continue", type="DirectLink", bindings = {
        "renderer=ognl:@org.iana.web.tapestry.form.FormLinkRenderer@RENDERER", "listener=listener:proceed"})
    public abstract IComponent getContinueComponent();

    @Persist("client")
    public abstract RzmCallback getCallBack();
    public abstract void setCallBack(RzmCallback callback);

    @Persist("client")
    public abstract void setShowContinue(boolean value);

    public MyChangePasswordListener getListener(){
        return this;
    }

    public void cancel(){
        getCallBack().performCallback(getRequestCycle());        
    }

    public void proceed(){
        getCallBack().performCallback(getRequestCycle());
    }

    public void changePassword(String currentPassword, String newPassword, String confirmNewPassword) {
        try {
            getRzmServices().changePassword(getVisitState().getUser().getUserName(), currentPassword, newPassword, confirmNewPassword);
            setInfoMessage(getMessageUtil().passwordChangeSuccessfull());
            setShowContinue(true);
        } catch (PasswordChangeException e) {
            setErrorMessage(e.getMessage());
        }

    }


    public void reportError(IFormComponent id, String message) {
        setErrorField(id, message);
    }

}
