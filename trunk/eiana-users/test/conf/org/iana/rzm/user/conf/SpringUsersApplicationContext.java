/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.user.conf;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringUsersApplicationContext {
    public static final String CONFIG_FILE_NAME = "eiana-users-spring.xml";

    private static SpringUsersApplicationContext instance;
    private ApplicationContext appCtx;

    private SpringUsersApplicationContext() {
    }

    public static SpringUsersApplicationContext getInstance() {
        if (instance == null)
            instance = new SpringUsersApplicationContext();
        return instance;
    }

    public ApplicationContext getContext() {
        if (appCtx == null)
            appCtx = new ClassPathXmlApplicationContext(CONFIG_FILE_NAME);
        return appCtx;
    }
}
