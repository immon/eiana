package org.iana.rzm.conf;

import org.hibernate.SessionFactory;
import org.springframework.context.ApplicationContext;
import org.testng.annotations.Test;

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
