package org.iana.rzm.user.dao.accuracy;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.user.RoleManager;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.conf.SpringUsersApplicationContext;

/**
 * @author: Piotr Tkaczyk
 */
@Test(sequential=true, groups = {"RoleManagerBeanTest", "eiana-users"})
public class RoleManagerBeanTest {

    RoleManager roleManager;

    Role firstRole, secondRole;

    @BeforeClass
    public void init() {
        roleManager = (RoleManager) SpringUsersApplicationContext.getInstance().getContext().getBean("roleManager");
    }

    @Test
    public void testCreateRole() {
        firstRole = new SystemRole(SystemRole.SystemType.AC, "roleac", false, false);
        roleManager.createRole(firstRole);

        Role retRole = roleManager.getRole(firstRole.getObjId());
        assert retRole != null;
        assert retRole instanceof SystemRole;
        assert retRole.getType().equals(SystemRole.SystemType.AC);
        assert ((SystemRole)retRole).getName().equals("roleac");
        assert !((SystemRole) retRole).isMustAccept();
        assert !((SystemRole)retRole).isNotify();

        secondRole = new AdminRole(AdminRole.AdminType.IANA);
        roleManager.createRole(secondRole);

        retRole = roleManager.getRole(secondRole.getObjId());
        assert retRole != null;
        assert retRole.getType().equals(AdminRole.AdminType.IANA);
    }

    @Test (dependsOnMethods = {"testCreateRole"})
    public void testUpdateRole() {
        SystemRole systemRole = (SystemRole) roleManager.getRole(firstRole.getObjId());
        systemRole.setMustAccept(true);
        systemRole.setAcceptFrom(true);
        systemRole.setNotify(true);
        systemRole.setType(SystemRole.SystemType.TC);
        roleManager.updateRole(systemRole);

        Role retRole = roleManager.getRole(firstRole.getObjId());
        assert retRole != null;
        assert retRole instanceof SystemRole;
        assert retRole.getType().equals(SystemRole.SystemType.TC);
        assert ((SystemRole) retRole).getName().equals("roleac");
        assert ((SystemRole) retRole).isMustAccept();
        assert ((SystemRole) retRole).isNotify();
        assert ((SystemRole) retRole).isAcceptFrom();

        AdminRole adminRole = (AdminRole) roleManager.getRole(secondRole.getObjId());
        adminRole.setType(AdminRole.AdminType.GOV_OVERSIGHT);
        roleManager.updateRole(adminRole);

        adminRole = (AdminRole) roleManager.getRole(secondRole.getObjId());
        assert adminRole.getType().equals(AdminRole.AdminType.GOV_OVERSIGHT);
    }

    @AfterClass
    public void cleanUp() {
        roleManager.deleteRole(firstRole);
        roleManager.deleteRole(secondRole);
    }
}
