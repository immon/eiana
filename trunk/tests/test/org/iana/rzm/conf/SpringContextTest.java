package org.iana.rzm.conf;

import org.testng.annotations.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.hibernate.SessionFactory;

import java.io.File;

/**
 * Tests a syntax of spring services configuration.
 *
 * @author Patrycja Wegrzynowicz
 */
@Test(groups = {"spring-test"})
public class SpringContextTest {

    @Test
    public void testSpringServiceConfig() {
        ApplicationContext appContext = SpringApplicationContext.getInstance().getContext();
        assert appContext != null;
        SessionFactory sessionFactory = (SessionFactory) appContext.getBean("sessionFactory");
        assert sessionFactory != null;
       
    }
}
