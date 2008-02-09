package org.iana.rzm.web.pages.admin;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.common.admin.*;
import org.iana.rzm.web.model.*;

public abstract class CreateAdminUser extends AdminPage  implements PageBeginRenderListener, UserAttributeEditor {

    @Component(id="userEditor", type="AdminUserEditor", bindings = {"create=literal:true", "listener=prop:editor", "user=prop:user"})
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
