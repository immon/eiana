package org.iana.rzm.web.admin.components;

import org.apache.tapestry.IAsset;
import org.apache.tapestry.IComponent;
import org.apache.tapestry.annotations.Asset;
import org.apache.tapestry.annotations.Component;
import org.apache.tapestry.annotations.ComponentClass;
import org.apache.tapestry.event.PageEvent;
import org.apache.tapestry.form.IPropertySelectionModel;
import org.iana.commons.ListUtil;
import org.iana.rzm.web.admin.model.AdminRoleVOWrapper;
import org.iana.rzm.web.admin.model.AdminUserVOWraper;
import org.iana.rzm.web.common.model.RoleVOWrapper;
import org.iana.rzm.web.common.model.UserVOWrapper;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@ComponentClass
public abstract class AdminUserEditor extends UserEditor {

    public static final RolesSelectionModel ROLES = new RolesSelectionModel();

    @Component(id = "roles", type = "PropertySelection", bindings = {
        "model=ognl:@org.iana.rzm.web.admin.components.AdminUserEditor@ROLES", "value=prop:role",
        "displayName=message:roles-label"
        })
    public abstract IComponent getRolesComponent();

    @Asset(value = "WEB-INF/admin/AdminUserEditor.html")
    public abstract IAsset get$template();

    public abstract void setRole(AdminRoleVOWrapper.AdminType type);
    public abstract AdminRoleVOWrapper.AdminType getRole();

    public void pageBeginRender(PageEvent event) {
        super.pageBeginRender(event);
        AdminUserVOWraper user = (AdminUserVOWraper) getUser();
        List<AdminRoleVOWrapper> list = user.getAdminRoles();
        if (list.size() > 0) {
            setRole((AdminRoleVOWrapper.AdminType) list.get(0).getType());
        }
    }

    public void save() {

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

        AdminRoleVOWrapper.AdminType role = getRole();
        List<RoleVOWrapper> roles = new ArrayList<RoleVOWrapper>();
        roles.add(new AdminRoleVOWrapper(role));
        user.setRoles(roles);
        getListener().save(user);
    }



    private static class RolesSelectionModel implements IPropertySelectionModel, Serializable {
        private AdminRoleVOWrapper.AdminType[] roles;

        public RolesSelectionModel() {
            roles = ListUtil.findMultiple(Arrays.asList(AdminRoleVOWrapper.AdminType.values()),
                    new ListUtil.Predicate<AdminRoleVOWrapper.AdminType>(){
                        public boolean evaluate(AdminRoleVOWrapper.AdminType o) {
                            return o.ordinal() != AdminRoleVOWrapper.AdminType.ROOT.ordinal();
                        }
                    }).toArray(new AdminRoleVOWrapper.AdminType[0]);
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
            return AdminRoleVOWrapper.AdminType.valueOf(value);
        }
    }
}
