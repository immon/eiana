package org.iana.rzm.user.dao.accuracy;

import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.AdminUser;
import org.iana.rzm.user.dao.UserDAO;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Piotr Tkaczyk
 */

public class UserManagerBeanAccuracyTest {
    UserManager manager;
    Long        userId;

    @BeforeClass
    public void init() {
        manager = (UserManager) new ClassPathXmlApplicationContext("eiana-users-spring.xml").getBean("userManager");
    }

    @Test (groups = {"dao", "eiana-users"})
    public void testCreateUser() {
        RZMUser userCreated = new AdminUser();
        userCreated.setFirstName("Ivan");
        userCreated.setLastName("Delphin");
        userCreated.setLoginName("ivan123");
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
        assert userRetrived instanceof AdminUser;
        assert userRetrived.getFirstName().equals("Ivan");
        assert userRetrived.getLastName().equals("Delphin");
        assert userRetrived.getLoginName().equals("ivan123");
    }

    @Test(dependsOnMethods = {"testCreateUser"})
    public void testGetUserByName() throws Exception {
        RZMUser userRetrived = manager.get("ivan123");
        assert userRetrived != null;
        assert userRetrived instanceof AdminUser;
        assert userRetrived.getFirstName().equals("Ivan");
        assert userRetrived.getLastName().equals("Delphin");
        assert userRetrived.getLoginName().equals("ivan123");
    }
}
