package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.common.admin.*;
import org.iana.rzm.web.model.*;
import org.iana.rzm.web.tapestry.*;

public abstract class EditAdminUser extends AdminPage implements UserAttributeEditor, LinkTraget, PageBeginRenderListener, IExternalPage {

    @Component(id = "userEditor",
               type = "AdminUserEditor", bindings = {"create=literal:false", "listener=prop:editor", "user=prop:user"})
    public abstract IComponent getEditorComponent();

    @InjectPage("admin/Users")
    public abstract Users getUsersPage();

    @Persist("client:page")
    public abstract void setUserId(long id);
    public abstract long getUserId();

    public abstract void setUser(UserVOWrapper user);

     public void setIdentifier(Object id){
       setUserId((Long)id);
   }


    protected Object[] getExternalParameters() {
        return new Object[]{
            getUserId()
        };
    }

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle){
        if(parameters.length == 0){
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        Long userId = (Long) parameters[0];
        setUserId(userId);
    }

     public void pageBeginRender(PageEvent event){
         UserVOWrapper user = getAdminServices().getUser(getUserId());
         setUser(user);
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
