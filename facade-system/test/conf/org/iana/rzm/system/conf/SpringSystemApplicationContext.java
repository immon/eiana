/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.system.conf;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringSystemApplicationContext {
    private static SpringSystemApplicationContext instance;
    private ApplicationContext appCtx;

    private SpringSystemApplicationContext() {
    }

    public static SpringSystemApplicationContext getInstance() {
        if(instance == null)
            instance = new SpringSystemApplicationContext();
        return instance;
    }

    public ApplicationContext getContext() {
        if(appCtx == null)
            appCtx = new ClassPathXmlApplicationContext("spring-facade-system.xml");
        return appCtx;
    }
}
