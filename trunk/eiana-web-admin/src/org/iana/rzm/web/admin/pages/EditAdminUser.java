package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.callback.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.admin.model.*;
import org.iana.rzm.web.admin.pages.editors.*;
import org.iana.rzm.web.common.*;
import org.iana.rzm.web.common.model.*;

public abstract class EditAdminUser extends AdminPage implements UserAttributeEditor, LinkTraget, PageBeginRenderListener, IExternalPage {

    public static final String PAGE_NAME = "EditAdminUser";

    @Component(id = "userEditor", type = "AdminUserEditor", bindings = {"create=literal:false", "listener=prop:editor", "user=prop:user"})
    public abstract IComponent getEditorComponent();

    @InjectPage(Users.PAGE_NAME)
    public abstract Users getUsersPage();

    @Persist("client")
    public abstract void setUserId(long id);
    public abstract long getUserId();

    @Persist("client")
    public abstract void setCallback(ICallback callback);

    public abstract void setUser(UserVOWrapper user);

     public void setIdentifier(Object id){
       setUserId((Long)id);
   }


    protected Object[] getExternalParameters() {
        return new Object[]{
            getUserId(),getCallback()
        };
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){
        if(parameters.length == 0){
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        Long userId = (Long) parameters[0];
        setUserId(userId);
        setCallback((ICallback) parameters[1]);
    }

     public void pageBeginRender(PageEvent event){
         UserVOWrapper user = getAdminServices().getUser(getUserId());
         setUser(new AdminUserVOWraper(user.getVo()));
     }

    public void save(UserVOWrapper user){
        getAdminServices().updateUser(user);
        getRequestCycle().activate(getUsersPage());
    }

    public void revert(){
        getRequestCycle().activate(getUsersPage());
    }

    public UserAttributeEditor getEditor(){
        return this;
    }

}
