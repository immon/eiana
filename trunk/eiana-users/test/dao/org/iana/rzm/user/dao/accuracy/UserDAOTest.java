package org.iana.rzm.user.dao.accuracy;

import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.conf.SpringUsersApplicationContext;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterClass;

import java.util.List;
import java.util.Set;
import java.util.HashSet;


/**
 * org.iana.rzm.domain.dao.accuracy.UserDAOTest
 *
 * @author Marcin Zajaczkowski
 * @author Jakub Laszkiewicz
 */
@Test(sequential=true, groups = {"dao", "eiana-users", "UserDAOTest", "user"})
public class UserDAOTest {

    private UserDAO dao;
    private long crObjId;
    Set<RZMUser> usersMap;

    @BeforeClass
    public void init() {
        dao = (UserDAO) SpringUsersApplicationContext.getInstance().getContext().getBean("userDAO");
    }

    @Test
    public void testUserCreate() throws Exception {
        RZMUser userCreated = new RZMUser();
        userCreated.setFirstName("Geordi");
        userCreated.setLastName("LaForge");
        dao.create(userCreated);
        crObjId = userCreated.getObjId();
        RZMUser userRetrieved = dao.get(crObjId);
        System.out.println("userRetrieved: " + userRetrieved.getFirstName() + " " + userRetrieved.getLastName());
        assert "Geordi".equals(userRetrieved.getFirstName());
        assert "LaForge".equals(userRetrieved.getLastName());
    }

    @Test(dependsOnMethods = {"testUserCreate"})
    public void testUserUpdate() throws Exception {
        RZMUser userRetrieved = dao.get(crObjId);
        assert userRetrieved != null;
        userRetrieved.setFirstName("John");
        dao.update(userRetrieved);

        RZMUser userRetrieved2 = dao.get(crObjId);
        //Note: != is used to compare object reference (to ensure that hibernate doesn't return the same object)
        assert userRetrieved != userRetrieved2;
        assert "John".equals(userRetrieved2.getFirstName());
    }

    @Test(dependsOnMethods = {"testUserUpdate"})
    public void testUserDelete() throws Exception {
        RZMUser userRetrieved = dao.get(crObjId);
        assert userRetrieved != null;
        dao.delete(userRetrieved);
        userRetrieved = dao.get(crObjId);
        assert userRetrieved == null;
    }

    @Test
    public void testFindUsersInRole() {
        usersMap = new HashSet<RZMUser>();
        usersMap.add(UserManagementTestUtil.createUser("DAOsys1", UserManagementTestUtil.createSystemRole("DAOaaa", true, true, SystemRole.SystemType.AC)));
        usersMap.add(UserManagementTestUtil.createUser("DAOsys2", UserManagementTestUtil.createSystemRole("DAOaaa", true, false, SystemRole.SystemType.TC)));
        usersMap.add(UserManagementTestUtil.createUser("DAOsys3", UserManagementTestUtil.createSystemRole("DAOaaa", false, false, SystemRole.SystemType.SO)));
        usersMap.add(UserManagementTestUtil.createUser("DAOsys4", UserManagementTestUtil.createSystemRole("DAOaaa", true, true, SystemRole.SystemType.TC)));
        usersMap.add(UserManagementTestUtil.createUser("DAOadmin1", new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)));
        usersMap.add(UserManagementTestUtil.createUser("DAOadmin2", new AdminRole(AdminRole.AdminType.IANA)));

        for(RZMUser user : usersMap)
            dao.create(user);

        List<RZMUser> result = dao.findUsersInSystemRole("DAOaaa", SystemRole.SystemType.AC, true, true);
        assert result.size() == 1;
        RZMUser user = result.iterator().next();
        assert "user-DAOsys1".equals(user.getLoginName());

        result = dao.findUsersInSystemRole("DAOaaa", SystemRole.SystemType.TC, true, false);
        assert result.size() == 2;
        Set<String> loginNames = new HashSet<String>();
        for (RZMUser u : result) loginNames.add(u.getLoginName());
        assert loginNames.contains("user-DAOsys2") && loginNames.contains("user-DAOsys4");

        result = dao.findUsersInSystemRole("DAOaaa", SystemRole.SystemType.SO, false, false);
        assert result.size() == 1;
        user = result.iterator().next();
        assert "user-DAOsys3".equals(user.getLoginName());

        result = dao.findUsersInSystemRole("DAOaaa", SystemRole.SystemType.TC, true, true);
        assert result.size() == 1;
        user = result.iterator().next();
        assert "user-DAOsys4".equals(user.getLoginName());

        result = dao.findUsersInAdminRole(AdminRole.AdminType.GOV_OVERSIGHT);
        assert result.size() == 1;
        user = result.iterator().next();
        assert "user-DAOadmin1".equals(user.getLoginName());

        result = dao.findUsersInAdminRole(AdminRole.AdminType.IANA);
        assert result.size() == 1;
        user = result.iterator().next();
        assert "user-DAOadmin2".equals(user.getLoginName());
    }

    @AfterClass
    private void cleanUp() {
        for(RZMUser user : usersMap)
            dao.delete(user);
    }
}
