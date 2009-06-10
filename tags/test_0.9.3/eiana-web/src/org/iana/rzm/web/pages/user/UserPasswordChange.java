package org.iana.rzm.web.pages.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.form.*;
import org.iana.rzm.facade.passwd.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.tapestry.*;

public abstract class UserPasswordChange extends UserPage implements MyChangePasswordListener{
    public static final String PAGE_NAME = "user/UserPasswordChange";

        @Component(id="changePassword", type="MyPasswordChangeEditor", bindings = {"listener=prop:listener"})
        public abstract IComponent getChangePasswordEditorComponent();

        @Component(id="continue", type="DirectLink", bindings = {
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER", "listener=listener:proceed"})
        public abstract IComponent getContinueComponent();

        @Persist("client:page")
        public abstract RzmCallback getCallBack();
        public abstract void setCallBack(RzmCallback callback);

        @Persist("client:page")
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
                setInfoMessage("Your Password was change successfully");
                setShowContinue(true);
            } catch (PasswordChangeException e) {
                setErrorMessage(e.getMessage());
            }

        }


        public void reportError(IFormComponent id, String message) {
            setErrorField(id, message);
        }

}
