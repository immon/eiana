package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.admin.model.*;
import org.iana.rzm.web.admin.pages.editors.*;
import org.iana.rzm.web.common.model.*;

public abstract class CreateAdminUser extends AdminPage  implements PageBeginRenderListener, UserAttributeEditor {
    public static final String PAGE_NAME = "CreateAdminUser";

    @Component(id="userEditor", type="AdminUserEditor", bindings = {"create=literal:true", "listener=prop:editor", "user=prop:user"})
    public abstract IComponent getEditorComponent();

    @InjectPage(Users.PAGE_NAME)
    public abstract Users getUsersPage();

    public abstract void setUser(AdminUserVOWraper user);

     public void pageBeginRender(PageEvent event){
         setUser(new AdminUserVOWraper());
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
