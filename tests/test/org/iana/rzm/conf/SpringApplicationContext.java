package org.iana.rzm.conf;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;

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
            appCtx = new FileSystemXmlApplicationContext("conf/spring/services-config.xml");
        return appCtx;
    }
}
