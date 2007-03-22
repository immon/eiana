package org.iana.rzm.user.dao.accuracy;

import org.iana.rzm.user.*;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Piotr Tkaczyk
 */
@Test (sequential=true, groups = {"UserManagerBean", "dao", "eiana-users"})
public class UserManagerBeanAccuracyTest {
    UserManager manager;
    Long        userId;

    @BeforeClass
    public void init() {
        manager = (UserManager) new ClassPathXmlApplicationContext("eiana-users-spring.xml").getBean("userManager");
    }

    @Test
    public void testCreateUser() {
        RZMUser userCreated = new RZMUser();
        userCreated.setFirstName("Ivan");
        userCreated.setLastName("Delphin");
        userCreated.setLoginName("ivan123");
        userCreated.addRole(new AdminRole(AdminRole.AdminType.GOV_OVERSIGHT));
        manager.create(userCreated);
        userId = userCreated.getObjId();
        RZMUser userRetrieved = manager.get(userId);

        assert userRetrieved.getFirstName().equals("Ivan");
        assert userRetrieved.getLastName().equals("Delphin");
    }

    @Test(dependsOnMethods = {"testCreateUser"})
    public void testGetUserById() throws Exception {
        RZMUser userRetrived = manager.get(userId);
        assert userRetrived != null;
        assert userRetrived.getRoles().size() == 1;
        Role role = userRetrived.getRoles().iterator().next();
        assert role instanceof AdminRole;
        AdminRole ar = (AdminRole) role;
        assert AdminRole.AdminType.GOV_OVERSIGHT.equals(ar.getTrackData());
        assert userRetrived.getFirstName().equals("Ivan");
        assert userRetrived.getLastName().equals("Delphin");
        assert userRetrived.getLoginName().equals("ivan123");
    }

    @Test(dependsOnMethods = {"testGetUserById"})
    public void testGetUserByName() throws Exception {
        RZMUser userRetrived = manager.get("ivan123");
        assert userRetrived != null;
        assert userRetrived.getRoles().size() == 1;
        Role role = userRetrived.getRoles().iterator().next();
        assert role instanceof AdminRole;
        AdminRole ar = (AdminRole) role;
        assert AdminRole.AdminType.GOV_OVERSIGHT.equals(ar.getTrackData());
        assert userRetrived.getFirstName().equals("Ivan");
        assert userRetrived.getLastName().equals("Delphin");
        assert userRetrived.getLoginName().equals("ivan123");
    }
}
