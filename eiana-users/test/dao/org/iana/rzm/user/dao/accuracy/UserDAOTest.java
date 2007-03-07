package org.iana.rzm.user.dao.accuracy;

import org.iana.rzm.user.dao.UserDAO;
import org.iana.rzm.user.User;
import org.iana.rzm.user.AdminUser;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;


/**
 * org.iana.rzm.domain.dao.accuracy.UserDAOTest
 *
 * @author Marcin Zajaczkowski
 */
@Test(groups = {"dao", "eiana-users"})
public class UserDAOTest {

    private UserDAO dao;
    private long crObjId;

    @BeforeClass
    public void init() {
        dao = (UserDAO) new ClassPathXmlApplicationContext("spring.xml").getBean("userDAO");
    }

    @Test
    public void testCreate() throws Exception {
        User userCreated = new AdminUser();
        userCreated.setFirstName("Geordi");
        userCreated.setLastName("LaForge");
        dao.create(userCreated);
        crObjId = userCreated.getObjId();
        User userRetrieved = dao.get(crObjId);
        System.out.println("userRetrieved: " + userRetrieved.getFirstName() + " " + userRetrieved.getLastName());
        assert "Geordi".equals(userRetrieved.getFirstName());
        assert "LaForge".equals(userRetrieved.getLastName());
    }

    @Test(dependsOnMethods = {"testCreate"})
    public void testUpdate() throws Exception {
        User userRetrieved = dao.get(crObjId);
        assert userRetrieved != null;
        userRetrieved.setFirstName("John");
        dao.update(userRetrieved);

        User userRetrieved2 = dao.get(crObjId);
        //Note: != is used to compare object reference (to ensure that hibernate doesn't return the same object)
        assert userRetrieved != userRetrieved2;
        assert "John".equals(userRetrieved2.getFirstName());
    }

    @Test(dependsOnMethods = {"testCreate"})
    public void testDelete() throws Exception {
        User userRetrieved = dao.get(crObjId);
        assert userRetrieved != null;
        dao.delete(userRetrieved);
        userRetrieved = dao.get(crObjId);
        assert userRetrieved == null;
    }
}
