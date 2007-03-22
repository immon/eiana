/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans;

import org.testng.annotations.Test;
import org.testng.annotations.BeforeTest;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.context.ApplicationContext;
import org.iana.rzm.domain.dao.DomainDAO;
import org.iana.rzm.domain.Domain;

import java.net.URL;

@Test(sequential=true, groups = {"jbpm", "eiana-trans"})
public class JbpmUpdateDomainTest {

    ApplicationContext ctx;
    JbpmUpdateDomain updateDomainTest;

    @BeforeTest
    public void init() {
        ctx = new ClassPathXmlApplicationContext("eiana-trans-spring.xml");
        updateDomainTest = (JbpmUpdateDomain) ctx.getBean("UpdateDomainTest");
        updateDomainTest.setContext(ctx);
    }

    @Test
    public void testUpdateDomain() throws Exception {
        assert updateDomainTest.doUpdate();
    }
}
