package org.iana.rzm.conf;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author Piotr Tkaczyk
 */

public class SpringApplicationContext {
    private static SpringApplicationContext instance;
    private ApplicationContext appCtx;

    private SpringApplicationContext() {
    }

    public static SpringApplicationContext getInstance() {
        if(instance == null)
            instance = new SpringApplicationContext();
        return instance;
    }

    public ApplicationContext getContext() {
        if(appCtx == null)
            appCtx = new ClassPathXmlApplicationContext("services-config.xml");
        return appCtx;
    }
}
