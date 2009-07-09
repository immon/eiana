package org.iana.rzm.web.admin.pages;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.IExternalPage;
import org.apache.tapestry.IRequestCycle;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.InjectPage;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.callback.ICallback;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.web.admin.model.AdminUserVOWraper;
import org.iana.rzm.web.admin.pages.editors.UserAttributeEditor;
import org.iana.rzm.web.common.LinkTraget;
import org.iana.rzm.web.common.model.UserVOWrapper;

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
            return;
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
