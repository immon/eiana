package org.iana.rzm.web.pages.user;

import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.iana.rzm.web.model.*;

import java.util.*;

public abstract class UserAccess extends UserPage implements PageBeginRenderListener, IExternalPage {

    @Component(id = "userList",
               type = "For",
               bindings = {"source=prop:userList", "value=ognl:userAccess", "element=literal:tr"})
    public abstract IComponent getUsersListComponent();

    @Component(id = "userName", type = "Insert", bindings = {"value=ognl:userName"})
    public abstract IComponent getuserNameComponent();

    @Component(id = "roles", type = "Insert", bindings = {"value=ognl:roles"})
    public abstract IComponent getRolesComponent();

    @Component(id = "status", type = "Insert", bindings = {"value=ognl:status"})
    public abstract IComponent getStatsComponent();

    @Component(id = "action", type = "Insert", bindings = {"value=prop:actionTitle"})
    public abstract IComponent getActionComponent();

    @Component(id = "domainName", type = "Insert", bindings = {"value=prop:domainName"})
    public abstract IComponent getDomainNameComponent();

    @Component(id = "country", type = "Insert", bindings = {"value=prop:countryName"})
    public abstract IComponent getCountryNameComponent();

    @Component(id = "changeStatus", type = "DirectLink", bindings = {
        "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER",
        "listener=listener:changeAccess",
        "disabled=prop:linkDisabled",
        "parameters={components.userList.value.userId,components.userList.value.enabled}"})
    public abstract IComponent getEnabledComponent();

    @Component(id = "back", type = "OverviewLink", bindings = {
        "title=literal:User Access Settings", "page=prop:home", "actionTitle=literal:Back to Overview >"})
    public abstract IComponent getBackComponent();

    @InjectPage("user/UserHome")
    public abstract UserHome getHome();

    @Persist("client:page")
    public abstract void setDomainId(long domainId);

    public abstract long getDomainId();

    @Persist("client:page")
    public abstract void setDomainName(String name);

    public abstract String getDomainName();

    public abstract void setUserList(List<UserAccessValue> users);

    public abstract UserAccessValue getUserAccess();

    public abstract void setCountryName(String countryName);

    public void activateExternalPage(Object[] parameters, IRequestCycle cycle) {
        if (parameters.length == 0 || parameters.length < 2) {
            getExternalPageErrorHandler().handleExternalPageError(getMessageUtil().getSessionRestorefailedMessage());
        }

        Long id = Long.parseLong(parameters[0].toString());
        String domainName = parameters[1].toString();
        setDomainId(id);
        setDomainName(domainName);
    }

    public void pageBeginRender(PageEvent event) {
        List<UserVOWrapper> usersForDomain = getUserServices().getUsersForDomain(getDomainName());
        Set<UserAccessValue> users = new HashSet<UserAccessValue>();
        for (UserVOWrapper userVOWrapper : usersForDomain) {
            users.add(new UserAccessValue(
                userVOWrapper.getId(),
                userVOWrapper.getUserName(),
                userVOWrapper.listSystemRolesForDomain(getDomainName()),
                userVOWrapper.isAccessEnabled(getDomainName())));
        }
        setUserList(new ArrayList<UserAccessValue>(users));
        setCountryName("(" + getUserServices().getCountryName(getDomainName()) + ")");
    }


    public String getStatus() {
        return getUserAccess().isEnabled() ? "Enable" : "Disable";
    }

    public String getRoles() {
        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (String o : getUserAccess().getRoleNames()) {
            builder.append(o);
            if (getUserAccess().getRoleNames().size() > 0 && index < getUserAccess().getRoleNames().size()) {
                builder.append(" ");
            }

            index++;
        }

        return builder.toString();
    }

    public String getActionTitle() {
        return getUserAccess().getUserId() == getVisitState().getUserId() ? "" :
               getUserAccess().isEnabled() ? "Disable" : "Enable";
    }

    public String getUserName() {
        return getUserAccess().getUserName();
    }

    public boolean isLinkDisabled(){
        return getUserAccess().getUserId() == getVisitState().getUserId();
    }

    public void changeAccess(long userId, boolean currentState) {
        getUserServices().setAccessToDomain(getDomainId(), userId, !currentState);
    }

    protected Object[] getExternalParameters() {
        return new Object[]{
            getDomainId(), getDomainName()
        };
    }

    public String getLinkClass(){
        return getUserAccess().getUserId() == getVisitState().getUserId() ?  "buttonDisabled" : "button";
    }

    public String getButtonRightClass(){
        return getUserAccess().getUserId() == getVisitState().getUserId() ?  "" : "right";
    }

    public String getButtonLeftClass(){
        return getUserAccess().getUserId() == getVisitState().getUserId() ?  "" : "left";
    }

}

class UserAccessValue extends ValueObject {

    private long userId;
    private String userName;
    private List<String> roleNames;
    private boolean enabled;

    public UserAccessValue(long userId, String userName, List<String> roleNames, boolean enabled) {
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
