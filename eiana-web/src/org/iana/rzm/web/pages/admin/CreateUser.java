package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.web.common.admin.UserAttributeEditor;
import org.iana.rzm.web.model.UserVOWrapper;


public abstract class CreateUser extends AdminPage implements PageBeginRenderListener, UserAttributeEditor {

    @Component(id="userEditor", type="UserEditor", bindings = {"create=literal:true", "listener=prop:editor", "user=prop:user"})
    public abstract IComponent getEditorComponent();

    @InjectPage("admin/Users")
    public abstract Users getUsersPage();

    public abstract void setUser(UserVOWrapper user);

     public void pageBeginRender(PageEvent event){
         setUser(new UserVOWrapper());
     }

    public void save(UserVOWrapper user){
        getAdminServices().createUser(user);
        getRequestCycle().activate(getUsersPage());
    }

    public void revert(){
        getRequestCycle().activate(getUsersPage());
    }

    public UserAttributeEditor getEditor(){
        return this;
    }


}
