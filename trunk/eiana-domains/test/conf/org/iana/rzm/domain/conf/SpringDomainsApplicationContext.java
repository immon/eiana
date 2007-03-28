/**
 * @author Piotr Tkaczyk
 */
package org.iana.rzm.domain.conf;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SpringDomainsApplicationContext {

    private static SpringDomainsApplicationContext instance;
    private ApplicationContext appCtx;

    private SpringDomainsApplicationContext() {
    }

    public static SpringDomainsApplicationContext getInstance() {
        if(instance == null)
            instance = new SpringDomainsApplicationContext();
        return instance;
    }

    public ApplicationContext getContext() {
        if(appCtx == null)
            appCtx = new ClassPathXmlApplicationContext("eiana-domains-spring.xml");
        return appCtx;
    }
}
