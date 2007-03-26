/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.trans.conf;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

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
            appCtx = new ClassPathXmlApplicationContext("eiana-trans-spring.xml");
        return appCtx;
    }
}
