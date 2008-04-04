package org.iana.rzm.web.components.admin;

import org.apache.commons.lang.*;
import org.apache.tapestry.*;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.*;
import org.apache.tapestry.form.*;
import org.iana.dns.validator.*;
import org.iana.rzm.web.model.*;

import java.io.*;
import java.util.*;

@ComponentClass()
public abstract class SystemUserEditor extends UserEditor implements PageBeginRenderListener {

    public static final RolesSelectionModel ROLES = new RolesSelectionModel();

    @Asset(value = "WEB-INF/admin/SystemUserEditor.html")
    public abstract IAsset get$template();

    @Asset("images/checkbox_on.png")
    public abstract IAsset getCheckboxOn();

    @Asset("images/checkbox_off.png")
    public abstract IAsset getCheckboxOff();

    @Component(id = "domainsRoles",
               type = "For",
               bindings = {"source=prop:userDomains", "value=ognl:userDomain", "element=literal:tr"})
    public abstract IComponent getDomainsRolesComponent();

    @Component(id = "domain", type = "Insert", bindings = {"value=ognl:components.domainsRoles.value.domainName"})
    public abstract IComponent getDomainComponent();

    @Component(id = "role", type = "Insert", bindings = {"value=components.domainsRoles.value.roleType"})
    public abstract IComponent getRoleComponent();

    @Component(id = "deleteRole", type = "LinkSubmit", bindings = {
        "action=listener:deleteRole", "tag=literal:deleteRole", "selected=prop:listenerTag",
        "parameters=components.domainsRoles.value.domainId"
        })
    public abstract IComponent getDeleteComponent();

    @Component(id = "addRole", type = "LinkSubmit", bindings = {
        "action=listener:addRole", "tag=literal:addRole", "selected=prop:listenerTag"})
    public abstract IComponent getAddComponent();

    @Component(id = "newdomain", type = "TextField", bindings = {"value=prop:newDomain"})
    public abstract IComponent getnewDomainComponent();

    @Component(id = "roles", type = "PropertySelection", bindings = {
        "model=ognl:@org.iana.rzm.web.components.admin.SystemUserEditor@ROLES", "value=prop:role"
        })
    public abstract IComponent getRolesComponent();

    @InjectComponent("newdomain")
    public abstract IFormComponent getNewDomainField();

    @Component(id = "notify", type = "Checkbox", bindings = {"value=prop:notify"})
    public abstract IComponent getNotifyComponent();

    @Component(id = "acceptFrom", type = "Checkbox", bindings = { "value=prop:acceptFrom"})
    public abstract IComponent getAcceptFromComponent();

    @Component(id = "mustAccept", type = "Checkbox", bindings = { "value=prop:mustAccept"})
    public abstract IComponent getMustAcceptComponent();

    @Component(id = "notifyImage", type = "Image", bindings = "image=prop:notifyImage")
    public abstract IComponent getNotifyImageComponent();

    @Component(id = "acceptFromImage", type = "Image", bindings = "image=prop:acceptFromImage")
    public abstract IComponent getAcceptFromImageComponent();

    @Component(id = "mustAcceptImage", type = "Image", bindings = "image=prop:mustAcceptImage")
    public abstract IComponent getMustAcceptImageComponent();

    @Persist("client:page")
    public abstract void setUserDomains(List<RoleUserDomain> userDomains);
    public abstract List<RoleUserDomain> getUserDomains();

    public abstract void setUserDomain(RoleUserDomain domain);

    public abstract RoleUserDomain getUserDomain();

    public abstract String getListenerTag();

    public abstract String getNewDomain();

    public abstract void setNewDomain(String domain);

    public abstract void setRole(SystemRoleVOWrapper.SystemType type);

    public abstract SystemRoleVOWrapper.SystemType getRole();

    public abstract boolean isNotify();
    public abstract void setNotify(boolean value);

    public abstract boolean isMustAccept();
    public abstract void setMustAccept(boolean value);

    @InitialValue("true")
    public abstract boolean isAcceptFrom();
    public abstract void setAcceptFrom(boolean value);

    public void pageBeginRender(PageEvent event) {
        super.pageBeginRender(event);
        UserVOWrapper user = getUser();
        if (!event.getRequestCycle().isRewinding() && getUserDomains() == null) {
            setUserDomains(user.getUserDomains());
        }
    }

    public IAsset getNotifyImage() {
        return getUserDomain().isNotify() ? getCheckboxOn() : getCheckboxOff();
    }

    public IAsset getAcceptFromImage() {
        return getUserDomain().isAcceptFrom() ? getCheckboxOn() : getCheckboxOff();
    }


    public IAsset getMustAcceptImage() {
        return getUserDomain().isMustAccept() ? getCheckboxOn() : getCheckboxOff();
    }

    public void addRole() {
        String domain = getNewDomain();
        SystemRoleVOWrapper.SystemType role = getRole();

        if (StringUtils.isBlank(domain)) {
            getListener().setErrorField(getNewDomainField(), "Please specify value for domain");
            return;
        }

        try {
            DomainNameValidator.validateName(domain);
        } catch (InvalidDomainNameException e) {
            getListener().setErrorField(getNewDomainField(),
                                        "Invalid Domain name " + e.getName() + " " + e.getReason());
            return;
        }

        List<RoleUserDomain> list = getUserDomains();
        for (RoleUserDomain userDomain : list) {
            if (userDomain.getDomainName().equals(domain) && role.toString().equals(userDomain.getRoleType())) {
                getListener().setErrorField(getNewDomainField(),
                                            "You already " + role.toString() + " for the domain " + domain);
                return;
            }
        }

        RoleUserDomain roleUserDomain = new RoleUserDomain(list.size(), domain, role.toString(), null, 0);

        roleUserDomain.setNotify(isNotify());
        roleUserDomain.setMustAccept(isMustAccept());
        roleUserDomain.setAcceptFrom(isAcceptFrom());

        list.add(roleUserDomain);
        setUserDomains(list);
        setNewDomain(null);
    }

    public void deleteRole(long id) {
        List<RoleUserDomain> list = getUserDomains();
        Iterator<RoleUserDomain> iterator = list.iterator();
        while (iterator.hasNext()) {
            RoleUserDomain userDomain = iterator.next();
            if (userDomain.getDomainId() == id) {
                iterator.remove();
            }
        }

        setUserDomains(list);
    }


    public void save() {

        String listenerTag = getListenerTag();
        boolean listenerInvoked = listenerTag != null &&
                                  (listenerTag.equals("addRole") || listenerTag.equals("deleteRole"));

        if (listenerInvoked) {
            return;
        }

        validateInput();
        if (getListener().getValidationDelegate().getHasErrors()) {
            return;
        }

        UserVOWrapper user = getUser();
        user.setUserName(getUserName());
        user.setFirstName(getName());
        user.setLastName(getLastName());
        user.setPublicKey(getPublicKey());
        user.setOrganization(getOrganization());
        user.setEmail(getEmail());
        user.setUseSecureId(isUseSecureId());

        if (isCreate()) {
            user.setPassword(getPassword());
        }

        List<RoleUserDomain> list = getUserDomains();
        List<RoleVOWrapper> roles = new ArrayList<RoleVOWrapper>();
        for (RoleUserDomain userDomain : list) {
            roles.add(userDomain.getRole());
        }

        user.setRoles(roles);
        getListener().save(user);
    }


    protected void validateInput() {
        if (getUserDomains() == null || getUserDomains().isEmpty()) {
            getListener().setErrorField(getNewDomainField(), "User should have at least one role in a domain");
        }
        super.validateInput();
    }

    private static class RolesSelectionModel implements IPropertySelectionModel, Serializable {
        private SystemRoleVOWrapper.SystemType[] roles;

        public RolesSelectionModel() {
            roles = SystemRoleVOWrapper.SystemType.values();
        }

        public int getOptionCount() {
            return roles.length;
        }

        public Object getOption(int index) {
            return roles[index];
        }

        public String getLabel(int index) {
            return roles[index].toString();
        }

        public String getValue(int index) {
            return roles[index].name();
        }

        public Object translateValue(String value) {
            return SystemRoleVOWrapper.SystemType.valueOf(value);
        }
    }
}
