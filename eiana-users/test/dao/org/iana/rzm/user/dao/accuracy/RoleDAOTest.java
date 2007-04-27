package org.iana.rzm.user.dao.accuracy;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;
import org.iana.rzm.user.dao.RoleDAO;
import org.iana.rzm.user.conf.SpringUsersApplicationContext;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.AdminRole;

/**
 * @author: Piotr Tkaczyk
 */
@Test(sequential=true, groups = {"dao", "eiana-users", "RoleDAOTest"})
public class RoleDAOTest {

    RoleDAO roleDAO;

    Role firstRole, secondRole;

    @BeforeClass
    public void init() {
        roleDAO = (RoleDAO) SpringUsersApplicationContext.getInstance().getContext().getBean("roleDAO");
    }

    @Test
    public void testCreateRole() {
        firstRole = new SystemRole(SystemRole.SystemType.AC, "roleac", false, false);
        roleDAO.createRole(firstRole);

        Role retRole = roleDAO.getRole(firstRole.getObjId());
        assert retRole != null;
        assert retRole instanceof SystemRole;
        assert retRole.getType().equals(SystemRole.SystemType.AC);
        assert ((SystemRole)retRole).getName().equals("roleac");
        assert !((SystemRole) retRole).isMustAccept();
        assert !((SystemRole)retRole).isNotify();

        secondRole = new AdminRole(AdminRole.AdminType.IANA);
        roleDAO.createRole(secondRole);

        retRole = roleDAO.getRole(secondRole.getObjId());
        assert retRole != null;
        assert retRole.getType().equals(AdminRole.AdminType.IANA);
    }

    @Test (dependsOnMethods = {"testCreateRole"})
    public void testUpdateRole() {
        SystemRole systemRole = (SystemRole) roleDAO.getRole(firstRole.getObjId());
        systemRole.setMustAccept(true);
        systemRole.setAcceptFrom(true);
        systemRole.setNotify(true);
        systemRole.setType(SystemRole.SystemType.TC);
        roleDAO.updateRole(systemRole);

        Role retRole = roleDAO.getRole(firstRole.getObjId());
        assert retRole != null;
        assert retRole instanceof SystemRole;
        assert retRole.getType().equals(SystemRole.SystemType.TC);
        assert ((SystemRole) retRole).getName().equals("roleac");
        assert ((SystemRole) retRole).isMustAccept();
        assert ((SystemRole) retRole).isNotify();
        assert ((SystemRole) retRole).isAcceptFrom();

        AdminRole adminRole = (AdminRole) roleDAO.getRole(secondRole.getObjId());
        adminRole.setType(AdminRole.AdminType.GOV_OVERSIGHT);
        roleDAO.updateRole(adminRole);

        adminRole = (AdminRole) roleDAO.getRole(secondRole.getObjId());
        assert adminRole.getType().equals(AdminRole.AdminType.GOV_OVERSIGHT);
    }

    @AfterClass
    public void cleanUp() {
        roleDAO.deleteRole(firstRole);
        roleDAO.deleteRole(secondRole);
    }
}
