package org.iana.rzm.web.components.admin;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry.BaseComponent;
import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.*;
import org.apache.tapestry.event.PageBeginRenderListener;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IFormComponent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.apache.tapestry.valid.IValidationDelegate;
import org.iana.dns.validator.DomainNameValidator;
import org.iana.dns.validator.InvalidDomainNameException;
import org.iana.rzm.web.common.admin.UserAttributeEditor;
import org.iana.rzm.web.model.RoleUserDomain;
import org.iana.rzm.web.model.SystemRoleVOWrapper;
import org.iana.rzm.web.model.UserVOWrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@ComponentClass()
public abstract class UserEditor extends BaseComponent implements PageBeginRenderListener {

    public static final RolesSelectionModel ROLES = new RolesSelectionModel();

    @Asset(value = "WEB-INF/admin/UserEditor.html")
    public abstract IAsset get$template();

    @Component(id = "userEditor", type = "Form", bindings = {
            "clientValidationEnabled=literal:true",
            "success=listener:save",
            //"cancel=listener:revert",
            "delegate=prop:validationDelegate"
            })
    public abstract IComponent getEditContactComponent();

    @Component(id = "username", type = "TextField", bindings = {"value=prop:userName", "displayName=message:username-label",
            "validators=validators:required"})
    public abstract IComponent getUserNameComponent();

    @Component(id = "password", type = "TextField",
            bindings = {"displayName=message:password-label", "value=prop:password", "validators=validators:required",
                    "hidden=literal:true", "disabled=prop:passwordDisabled"})
    public abstract IComponent getPasswordComponent();

    @Component(id = "confirmedPassword", type = "TextField",
            bindings = {"displayName=message:confirm-password-label", "value=prop:confirmPassword", "validators=validators:required",
                    "hidden=literal:true", "disabled=prop:passwordDisabled"})
    public abstract IComponent getConfirmedPasswordComponent();

    @Component(id = "name", type = "TextField", bindings = {"value=prop:name", "displayName=message:name-label",
            "validators=validators:required"})
    public abstract IComponent getNameComponent();

    @Component(id = "lastname", type = "TextField", bindings = {"value=prop:lastName", "displayName=message:lastname-label"})
    public abstract IComponent getLastNameComponent();

    @Component(id = "organization", type = "TextField", bindings = {"value=prop:organization", "displayName=message:organization-label"})
    public abstract IComponent getOrganizationComponent();

    @Component(id = "email", type = "TextField", bindings = {"value=prop:email", "displayName=message:email-label",
            "validators=validators:required, email"})
    public abstract IComponent getEmailComponent();

    @Component(id = "pgp", type = "TextArea", bindings = {"displayName=message:pgp-label", "value=prop:publicKey"})
    public abstract IComponent getPublicKeyComponent();

    @Component(id = "secureid", type = "Checkbox", bindings = {
            "displayName=message:secureid-label", "value=prop:useSecureId"})
    public abstract IComponent getSecureidComponent();

    @Component(id = "secureidLabel", type = "FieldLabel", bindings = {"field=component:secureid"})
    public abstract IComponent getRoleLabelComponent();

    @Component(id = "save", type = "LinkSubmit")
    public abstract IComponent getSubmitComponent();

    @Component(id = "cancel", type = "DirectLink", bindings = {"listener=listener:revert",
            "renderer=ognl:@org.iana.rzm.web.tapestry.form.FormLinkRenderer@RENDERER"})
    public abstract IComponent getCancelComponent();

    @Component(id = "domainsRoles", type = "For", bindings = {"source=prop:userDomains", "value=ognl:userDomain", "element=literal:tr"})
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
            "model=ognl:@org.iana.rzm.web.components.admin.UserEditor@ROLES", "value=prop:role"
            })
    public abstract IComponent getRolesComponent();

    @Asset("images/spacer.png")
    public abstract IAsset getSpacer();

    @InjectComponent("newdomain")
    public abstract IFormComponent getNewDomainField();

    @InjectComponent("password")
    public abstract IFormComponent getPasswordField();

    @Parameter(required = true)
    public abstract boolean isCreate();

    @Parameter(required = true)
    public abstract UserAttributeEditor getListener();

    @Parameter(required = true)
    public abstract UserVOWrapper getUser();

    @Persist("client:form")
    public abstract void setUserDomains(List<RoleUserDomain> userDomains);

    public abstract List<RoleUserDomain> getUserDomains();

    public abstract void setUserDomain(RoleUserDomain domain);

    public abstract void setUser(UserVOWrapper user);

    public abstract void setUserName(String userName);

    public abstract String getUserName();

    public abstract void setPassword(String password);

    public abstract String getPassword();

    public abstract void setConfirmPassword(String password);

    public abstract String getConfirmPassword();

    public abstract void setName(String name);

    public abstract String getName();

    public abstract void setLastName(String name);

    public abstract String getLastName();

    public abstract void setOrganization(String org);

    public abstract String getOrganization();

    public abstract void setEmail(String email);

    public abstract String getEmail();

    public abstract void setPublicKey(String key);

    public abstract String getPublicKey();

    public abstract void setUseSecureId(boolean value);

    public abstract boolean isUseSecureId();

    public abstract String getListenerTag();

    public abstract String getNewDomain();

    public abstract void setNewDomain(String domain);

    public abstract void setRole(SystemRoleVOWrapper.SystemType type);

    public abstract SystemRoleVOWrapper.SystemType getRole();

    public void pageBeginRender(PageEvent event) {

        UserVOWrapper user = getUser();

        if (!event.getRequestCycle().isRewinding() && getUserDomains() == null) {
            setUserName(user.getUserName());
            setName(user.getFirstName());
            setLastName(user.getLastName());
            setOrganization(user.getOrganization());
            setEmail(user.getEmail());
            setPublicKey(user.getPublickey());
            setUseSecureId(user.isUseSecureId());
            setUserDomains(user.getUserDomains());
        }
    }


    public IValidationDelegate getValidationDelegate() {
        return getListener().getValidationDelegate();
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
            getListener().setErrorField(getNewDomainField(), "Invalid Domain name " + e.getName() + " " + e.getReason());
            return;
        }

        List<RoleUserDomain> list = getUserDomains();
        for (RoleUserDomain userDomain : list) {
            if (userDomain.getDomainName().equals(domain) && role.toString().equals(userDomain.getRoleType())) {
                getListener().setErrorField(getNewDomainField(), "You already " + role.toString() + " for the domain " + domain);
                return;
            }
        }

        list.add(new RoleUserDomain(list.size(), domain, role.toString(), null, 0));
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


    public boolean isPasswordDisabled() {
        return !isCreate();
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
        List<SystemRoleVOWrapper> roles = new ArrayList<SystemRoleVOWrapper>();
        for (RoleUserDomain userDomain : list) {
            roles.add(userDomain.getRole());
        }

        user.setRoles(roles);
        getListener().save(user);
    }

    public void revert() {
        getListener().revert();
    }


    private void validateInput() {
        if(getUserDomains() == null || getUserDomains().isEmpty()){
             getListener().setErrorField(getNewDomainField(), "User should have at least one role in a domain");
        }

        if(isCreate()){
            String password = getPassword();
            String confirmPassword = getConfirmPassword();
            if(!StringUtils.equals(confirmPassword, password)){
                getListener().setErrorField(getPasswordField(), "Paswords are not the same");
            }
        }
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
