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
        roleManager.create(firstRole);

        Role retRole = roleManager.get(firstRole.getObjId());
        assert retRole != null;
        assert retRole instanceof SystemRole;
        assert retRole.getType().equals(SystemRole.SystemType.AC);
        assert ((SystemRole)retRole).getName().equals("roleac");
        assert !((SystemRole) retRole).isMustAccept();
        assert !((SystemRole)retRole).isNotify();

        secondRole = new AdminRole(AdminRole.AdminType.IANA);
        roleManager.create(secondRole);

        retRole = roleManager.get(secondRole.getObjId());
        assert retRole != null;
        assert retRole.getType().equals(AdminRole.AdminType.IANA);
    }

    @Test (dependsOnMethods = {"testCreateRole"})
    public void testUpdateRole() {
        SystemRole systemRole = (SystemRole) roleManager.get(firstRole.getObjId());
        systemRole.setMustAccept(true);
        systemRole.setAcceptFrom(true);
        systemRole.setNotify(true);
        systemRole.setType(SystemRole.SystemType.TC);
        roleManager.update(systemRole);

        Role retRole = roleManager.get(firstRole.getObjId());
        assert retRole != null;
        assert retRole instanceof SystemRole;
        assert retRole.getType().equals(SystemRole.SystemType.TC);
        assert ((SystemRole) retRole).getName().equals("roleac");
        assert ((SystemRole) retRole).isMustAccept();
        assert ((SystemRole) retRole).isNotify();
        assert ((SystemRole) retRole).isAcceptFrom();

        AdminRole adminRole = (AdminRole) roleManager.get(secondRole.getObjId());
        adminRole.setType(AdminRole.AdminType.GOV_OVERSIGHT);
        roleManager.update(adminRole);

        adminRole = (AdminRole) roleManager.get(secondRole.getObjId());
        assert adminRole.getType().equals(AdminRole.AdminType.GOV_OVERSIGHT);
    }

    @AfterClass
    public void cleanUp() {
        for (Role role : roleManager.findAll())
            roleManager.delete(role);
    }
}
