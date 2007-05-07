package org.iana.rzm.log;

import org.springframework.context.ApplicationContext;
import org.iana.rzm.facade.admin.AdminDomainService;
import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.conf.SpringApplicationContext;
import org.iana.rzm.domain.Domain;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Patrycja Wegrzynowicz
 */
public class LogTest {

    Logger logger;

    @BeforeClass
    public void init() {
        logger = (Logger) SpringApplicationContext.getInstance().getContext().getBean("logger");
    }

    @Test
    public void testLog() {
        logger.addLog("userName", "sessionID", "create domain", new Domain("new.domain"), new Object[]{});
    }
}
