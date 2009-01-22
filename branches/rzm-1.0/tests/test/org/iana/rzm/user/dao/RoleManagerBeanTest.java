package org.iana.rzm.user.dao;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.RoleManager;
import org.iana.rzm.user.SystemRole;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author: Piotr Tkaczyk
 */
@Test(sequential=true, groups = {"RoleManagerBeanTest", "eiana-users"})
public class RoleManagerBeanTest extends RollbackableSpringContextTest {

    protected RoleManager roleManager;

    Role firstRole, secondRole;

    public RoleManagerBeanTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    @BeforeClass
    public void init() {
//        roleManager = (RoleManager) SpringApplicationContext.getInstance().getContext().getBean("roleManager");
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

    @AfterClass (alwaysRun = true)
    public void cleanUp() {
//        for (Role role : roleManager.findAll())
//            roleManager.delete(role);
    }
}
