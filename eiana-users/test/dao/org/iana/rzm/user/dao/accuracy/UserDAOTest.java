package org.iana.rzm.user.dao.accuracy;

import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.dao.common.UserManagementTestUtil;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.AdminRole;
import org.iana.rzm.user.Role;
import org.iana.rzm.common.TrackData;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;

import java.util.List;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;
import java.sql.Timestamp;


/**
 * org.iana.rzm.domain.dao.accuracy.UserDAOTest
 *
 * @author Marcin Zajaczkowski
 * @author Jakub Laszkiewicz
 */
@Test(groups = {"dao", "eiana-users", "UserDAOTest"})
public class UserDAOTest {

    private UserDAO dao;
    private long crObjId;

    @BeforeClass
    public void init() {
        dao = (UserDAO) new ClassPathXmlApplicationContext("eiana-users-spring.xml").getBean("userDAO");
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
        dao.create(UserManagementTestUtil.createUser("sys1", UserManagementTestUtil.createSystemRole("aaa", true, true, SystemRole.SystemType.AC)));
        dao.create(UserManagementTestUtil.createUser("sys2", UserManagementTestUtil.createSystemRole("aaa", true, false, SystemRole.SystemType.TC)));
        dao.create(UserManagementTestUtil.createUser("sys3", UserManagementTestUtil.createSystemRole("aaa", false, false, SystemRole.SystemType.SO)));
        dao.create(UserManagementTestUtil.createUser("sys4", UserManagementTestUtil.createSystemRole("aaa", true, true, SystemRole.SystemType.TC)));
        dao.create(UserManagementTestUtil.createUser("admin1", new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT)));
        dao.create(UserManagementTestUtil.createUser("admin2", new AdminRole(AdminRole.AdminType.IANA)));

        List<RZMUser> result = dao.findUsersInSystemRole("aaa", SystemRole.SystemType.AC, true, true);
        assert result.size() == 1;
        RZMUser user = result.iterator().next();
        assert "user-sys1".equals(user.getLoginName());

        result = dao.findUsersInSystemRole("aaa", SystemRole.SystemType.TC, true, false);
        assert result.size() == 2;
        Set<String> loginNames = new HashSet<String>();
        for (RZMUser u : result) loginNames.add(u.getLoginName());
        assert loginNames.contains("user-sys2") && loginNames.contains("user-sys4");

        result = dao.findUsersInSystemRole("aaa", SystemRole.SystemType.SO, false, false);
        assert result.size() == 1;
        user = result.iterator().next();
        assert "user-sys3".equals(user.getLoginName());

        result = dao.findUsersInSystemRole("aaa", SystemRole.SystemType.TC, true, true);
        assert result.size() == 1;
        user = result.iterator().next();
        assert "user-sys4".equals(user.getLoginName());
    }
}
