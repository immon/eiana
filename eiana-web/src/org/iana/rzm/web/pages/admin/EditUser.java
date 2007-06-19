package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.web.common.admin.UserAttributeEditor;
import org.iana.rzm.web.model.UserVOWrapper;

public abstract class EditUser extends AdminPage implements PageBeginRenderListener, UserAttributeEditor  {


    @Component(id="userEditor", type="UserEditor", bindings = {"create=literal:false", "listener=prop:editor", "user=prop:user"})
    public abstract IComponent getEditorComponent();

    @InjectPage("admin/Users")
    public abstract Users getUsersPage();

    @Persist("client:form")
    public abstract long getUserId();
    public abstract void setUserId(long userId);

    public abstract void setUser(UserVOWrapper user);

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