package org.iana.rzm.trans.jbpm;

import org.jbpm.JbpmConfiguration;
import org.jbpm.configuration.ObjectFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

/**
 * @author Jakub Laszkiewicz
 */
public class JbpmSpringObjectFactory implements ObjectFactory,
        ApplicationContextAware {

    private static final long serialVersionUID = 1L;

    private ApplicationContext context;

    public JbpmSpringObjectFactory() {
        JbpmConfiguration.Configs.setDefaultObjectFactory(this);
    }

    public Object createObject(String name) {
        return context.getBean(name);
    }

    public boolean hasObject(String name) {
        return context.containsBean(name);
    }

    public void setApplicationContext(ApplicationContext context) {
        this.context = context;
    }
}
