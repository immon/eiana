package org.iana.config.conf;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringConfigApplicationContext {

    public static final String CONFIG_FILE_NAME = "eiana-config-spring.xml";

    private static SpringConfigApplicationContext instance;
    private ApplicationContext appCtx;

    private SpringConfigApplicationContext() {
    }

    public static SpringConfigApplicationContext getInstance() {
        if (SpringConfigApplicationContext.instance == null)
            SpringConfigApplicationContext.instance = new SpringConfigApplicationContext();
        return SpringConfigApplicationContext.instance;
    }

    public ApplicationContext getContext() {
        if (appCtx == null)
            appCtx = new ClassPathXmlApplicationContext(CONFIG_FILE_NAME);
        return appCtx;
    }
}
