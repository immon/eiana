/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.facade.accuracy;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringCommonApplicationContext {
    public static final String CONFIG_FILE_NAME = "spring-facade-common.xml";
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
            appCtx = new ClassPathXmlApplicationContext(CONFIG_FILE_NAME);
        return appCtx;
    }
}
