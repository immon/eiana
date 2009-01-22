package org.iana.rzm.user.dao;

import org.iana.criteria.And;
import org.iana.criteria.Criterion;
import org.iana.criteria.Equal;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.trans.UserManagementTestUtil;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.test.spring.RollbackableSpringContextTest;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * org.iana.rzm.domain.userDAO.accuracy.UserDAOTest
 *
 * @author Marcin Zajaczkowski
 * @author Jakub Laszkiewicz
 */
@Test(sequential=true, groups = {"dao", "eiana-users", "UserDAOTest", "user"})
public class UserDAOTest extends RollbackableSpringContextTest {

    protected UserDAO userDAO;
    private long crObjId;
    Set<RZMUser> usersMap;

    public UserDAOTest() {
        super(SpringApplicationContext.CONFIG_FILE_NAME);
    }

    protected void init() throws Exception {

//        userDAO = (UserDAO) SpringApplicationContext.getInstance().getContext().getBean("userDAO");

        usersMap = new HashSet<RZMUser>();
        usersMap.add(UserManagementTestUtil.createUser("DAOsys1", UserManagementTestUtil.createSystemRole("DAOaaa", true, true, SystemRole.SystemType.AC)));
        usersMap.add(UserManagementTestUtil.createUser("DAOsys2", UserManagementTestUtil.createSystemRole("DAOaaa", true, false, SystemRole.SystemType.TC)));
        usersMap.add(UserManagementTestUtil.createUser("DAOsys3", UserManagementTestUtil.createSystemRole("DAOaaa", false, false, SystemRole.SystemType.SO)));
        usersMap.add(UserManagementTestUtil.createUser("DAOsys4", UserManagementTestUtil.createSystemRole("DAOaaa", true, true, SystemRole.SystemType.TC)));
        usersMap.add(UserManagementTestUtil.createUser("DAOadmin1", new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)));
        usersMap.add(UserManagementTestUtil.createUser("DAOadmin2", new AdminRole(AdminRole.AdminType.IANA)));

        for(RZMUser user : usersMap)
            userDAO.create(user);
    }

    @Test
    public void testUserCreate() throws Exception {
        RZMUser userCreated = new RZMUser();
        userCreated.setFirstName("Geordi");
        userCreated.setLastName("LaForge");
        userDAO.create(userCreated);
        crObjId = userCreated.getObjId();
        RZMUser userRetrieved = userDAO.get(crObjId);
        System.out.println("userRetrieved: " + userRetrieved.getFirstName() + " " + userRetrieved.getLastName());
        assert "Geordi".equals(userRetrieved.getFirstName());
        assert "LaForge".equals(userRetrieved.getLastName());
    }

    @Test(dependsOnMethods = {"testUserCreate"})
    public void testUserUpdate() throws Exception {
        RZMUser userRetrieved = userDAO.get(crObjId);
        assert userRetrieved != null;
        userRetrieved.setFirstName("John");
        userDAO.update(userRetrieved);

        RZMUser userRetrieved2 = userDAO.get(crObjId);
        //Note: != is used to compare object reference (to ensure that hibernate doesn't return the same object)
//      not working with one transaction on whole class       
//        assert userRetrieved != userRetrieved2;
        assert "John".equals(userRetrieved2.getFirstName());
    }

    @Test(dependsOnMethods = {"testUserUpdate"})
    public void testUserDelete() throws Exception {
        RZMUser userRetrieved = userDAO.get(crObjId);
        assert userRetrieved != null;
        userDAO.delete(userRetrieved);
    }

    
    @Test
    public void testFindByCriteria_AdminRole() {
        Criterion crit = new Equal("role", "AdminRole");
        List<RZMUser> admins = userDAO.find(crit);
        assert admins.size() == 2;
    }

    @Test
    public void testFindByCriteria_IANARole() {
        Criterion crit = new And(new Equal("role", "AdminRole"), new Equal("role.type", "IANA"));
        List<RZMUser> admins = userDAO.find(crit);
        assert admins.size() == 1;
    }

    @Test
    public void testFindByCriteria_LoginName() {
        Criterion crit = new Equal("loginName", "user-DAOSys4");
        List<RZMUser> admins = userDAO.find(crit);
        assert admins.size() == 1;
    }

    @Test
    public void testFindUsersInRole() {
        List<RZMUser> result = userDAO.findUsersInSystemRole("DAOaaa", SystemRole.SystemType.AC, true, true, false);
        assert result.size() == 1;
        RZMUser user = result.iterator().next();
        assert "user-DAOsys1".equals(user.getLoginName());

        result = userDAO.findUsersInSystemRole("DAOaaa", SystemRole.SystemType.TC, true, false, false);
        assert result.size() == 2;
        Set<String> loginNames = new HashSet<String>();
        for (RZMUser u : result) loginNames.add(u.getLoginName());
        assert loginNames.contains("user-DAOsys2") && loginNames.contains("user-DAOsys4");

        result = userDAO.findUsersInSystemRole("DAOaaa", SystemRole.SystemType.SO, false, false, false);
        assert result.size() == 1;
        user = result.iterator().next();
        assert "user-DAOsys3".equals(user.getLoginName());

        result = userDAO.findUsersInSystemRole("DAOaaa", SystemRole.SystemType.TC, true, true, false);
        assert result.size() == 1;
        user = result.iterator().next();
        assert "user-DAOsys4".equals(user.getLoginName());

        result = userDAO.findUsersInAdminRole(AdminRole.AdminType.GOV_OVERSIGHT);
        assert result.size() == 1;
        user = result.iterator().next();
        assert "user-DAOadmin1".equals(user.getLoginName());

        result = userDAO.findUsersInAdminRole(AdminRole.AdminType.IANA);
        assert result.size() == 1;
        user = result.iterator().next();
        assert "user-DAOadmin2".equals(user.getLoginName());
    }

    @AfterClass (alwaysRun = true)
    public void cleanUp() {
//        for(RZMUser user : userDAO.findAll())
//            userDAO.delete(user);
    }
}
