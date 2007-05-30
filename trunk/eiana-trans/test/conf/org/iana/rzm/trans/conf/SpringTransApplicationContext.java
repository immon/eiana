/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans.conf;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringTransApplicationContext {
    public static final String CONFIG_FILE_NAME = "eiana-trans-spring.xml";

    private static SpringTransApplicationContext instance;
    private ApplicationContext appCtx;

    private SpringTransApplicationContext() {
    }

    public static SpringTransApplicationContext getInstance() {
        if(instance == null)
            instance = new SpringTransApplicationContext();
        return instance;
    }

    public ApplicationContext getContext() {
        if(appCtx == null)
            appCtx = new ClassPathXmlApplicationContext(CONFIG_FILE_NAME);
        return appCtx;
    }
}
