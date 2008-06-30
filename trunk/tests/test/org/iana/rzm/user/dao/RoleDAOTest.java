package org.iana.rzm.user.dao;

import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.Role;
import org.iana.rzm.user.SystemRole;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author: Piotr Tkaczyk
 */
@Test(sequential=true, groups = {"dao", "eiana-users", "RoleDAOTest"})
public class RoleDAOTest extends RollbackableSpringContextTest {

    protected RoleDAO roleDAO;

    Role firstRole, secondRole;


    public RoleDAOTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    @BeforeClass
    public void init() {
//        roleDAO = (RoleDAO) SpringApplicationContext.getInstance().getContext().getBean("roleDAO");
    }

    @Test
    public void testCreateRole() {
        firstRole = new SystemRole(SystemRole.SystemType.AC, "roleac", false, false);
        roleDAO.create(firstRole);

        Role retRole = roleDAO.get(firstRole.getObjId());
        assert retRole != null;
        assert retRole instanceof SystemRole;
        assert retRole.getType().equals(SystemRole.SystemType.AC);
        assert ((SystemRole)retRole).getName().equals("roleac");
        assert !((SystemRole) retRole).isMustAccept();
        assert !((SystemRole)retRole).isNotify();

        secondRole = new AdminRole(AdminRole.AdminType.IANA);
        roleDAO.create(secondRole);

        retRole = roleDAO.get(secondRole.getObjId());
        assert retRole != null;
        assert retRole.getType().equals(AdminRole.AdminType.IANA);
    }

    @Test (dependsOnMethods = {"testCreateRole"})
    public void testUpdateRole() {
        SystemRole systemRole = (SystemRole) roleDAO.get(firstRole.getObjId());
        systemRole.setMustAccept(true);
        systemRole.setAcceptFrom(true);
        systemRole.setNotify(true);
        systemRole.setType(SystemRole.SystemType.TC);
        roleDAO.update(systemRole);

        Role retRole = roleDAO.get(firstRole.getObjId());
        assert retRole != null;
        assert retRole instanceof SystemRole;
        assert retRole.getType().equals(SystemRole.SystemType.TC);
        assert ((SystemRole) retRole).getName().equals("roleac");
        assert ((SystemRole) retRole).isMustAccept();
        assert ((SystemRole) retRole).isNotify();
        assert ((SystemRole) retRole).isAcceptFrom();

        AdminRole adminRole = (AdminRole) roleDAO.get(secondRole.getObjId());
        adminRole.setType(AdminRole.AdminType.GOV_OVERSIGHT);
        roleDAO.update(adminRole);

        adminRole = (AdminRole) roleDAO.get(secondRole.getObjId());
        assert adminRole.getType().equals(AdminRole.AdminType.GOV_OVERSIGHT);
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp() {
//        for (Role role : roleDAO.findAll())
//            roleDAO.delete(role);
    }
}
