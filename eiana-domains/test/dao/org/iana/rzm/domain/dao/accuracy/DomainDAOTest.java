package org.iana.rzm.domain.dao.accuracy;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeClass;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.Domain;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import java.io.*;

/**
 * @author Patrycja Wegrzynowicz
 */
@Test(groups = {"dao", "eiana-domains"})
public class DomainDAOTest {

    private DomainDAO dao;

    @BeforeClass
    public void init() {
        dao = (DomainDAO) new ClassPathXmlApplicationContext("spring.xml").getBean("domainDAO");
    }

    @Test
    public void testCreate() throws Exception {
        Domain domainCreated = new Domain("iana.org");
        dao.create(domainCreated);
        Domain domainRetrieved = dao.get(domainCreated.getId());
        System.out.println("domainRetrieved: " + domainRetrieved.getName());
        assert "iana.org".equals(domainRetrieved.getName());
    }

    @Test(dependsOnMethods = {"testCreate"})
    public void testUpdate() throws Exception {
    }

    @Test(dependsOnMethods = {"testCreate"})
    public void testDelete() throws Exception {
    }

    public static void main(String[] args) {
        InputStream resource = new DomainDAOTest().getClass().getClassLoader().getResourceAsStream("spring.xml");
        BufferedReader in = new BufferedReader(new InputStreamReader(resource));
        String line;
        try {
            while ((line = in.readLine()) != null) {
                System.out.println(line);    
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("resource = " + resource);
        ClassPathXmlApplicationContext context  = new ClassPathXmlApplicationContext("spring.xml");
    }
}
