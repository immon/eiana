package org.iana.rzm.web.pages.user;

import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.Persist;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.iana.rzm.web.model.UserVOWrapper;
import org.iana.rzm.web.model.ValueObject;

import java.util.ArrayList;
import java.util.List;

public abstract class UserAccess extends UserPage implements PageBeginRenderListener {

    @Component(id = "userList", type = "For", bindings = {"source=prop:userList, value=prop:userAccess"})
    public abstract IComponent getUsersListComponent();

    @Component(id="enabled", type="DirectLink", bindings={"renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
            "listener=listener:enableAccess",
            "parameters=components.userList.value.userId"})
    public abstract IComponent getEnabledComponent();

    @Component(id="disabled", type="DirectLink", bindings={"renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
            "listener=listener:disableAccess",
            "parameters=components.userList.value.userId"})
    public abstract IComponent getDisabledComponent();

    @Persist("client:page")
    public abstract void setDomainId(long domainId);
    public abstract long getDomainId();


    public abstract void setUserList(List<UserAccessValue> users);
    public abstract UserAccessValue getUserAccess();

    public void pageBeginRender(PageEvent event){
        List<UserVOWrapper> usersForDomain = getUserServices().getUsersForDomain(getDomainId());
        List<UserAccessValue> users = new ArrayList<UserAccessValue>();
        for (UserVOWrapper userVOWrapper : usersForDomain) {
            users.add(new UserAccessValue(
                    userVOWrapper.getId(),
                    userVOWrapper.getUserName(),
                    userVOWrapper.listSystemUserRoles(),
                    userVOWrapper.isAccessEnabled()));
        }
        setUserList(users);
    }

    public void disableAccess(long userId){

    }

    public void enableAccess(long userId){

    }


}

class UserAccessValue extends ValueObject {

    private long    userId;
    private String  userName;
    private List<String> roleNames;
    private boolean enabled;

    public UserAccessValue(long userId, String userName, List<String> roleNames, boolean enabled){
        this.roleNames = roleNames;
        this.enabled = enabled;

        this.userName = userName;
        this.userId = userId;
    }

    public long getUserId() {
        return userId;
    }


    public String getUserName() {
        return userName;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public List<String> getRoleNames() {
        return roleNames;
    }
}
