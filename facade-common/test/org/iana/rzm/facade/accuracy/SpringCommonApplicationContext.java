/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.facade.accuracy;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringCommonApplicationContext {
    private static SpringCommonApplicationContext instance;
    private ApplicationContext appCtx;

    private SpringCommonApplicationContext() {
    }

    public static SpringCommonApplicationContext getInstance() {
        if(instance == null)
            instance = new SpringCommonApplicationContext();
        return instance;
    }

    public ApplicationContext getContext() {
        if(appCtx == null)
            appCtx = new ClassPathXmlApplicationContext("spring-facade-common.xml");
        return appCtx;
    }
}
