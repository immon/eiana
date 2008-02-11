package org.iana.rzm.trans.process.general.springsupport;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;

/**
 * @author Jakub Laszkiewicz
 */
public class JbpmContextFactoryBean implements JbpmContextFactory {
    private JbpmConfiguration jbpmConfiguration;


    public JbpmContextFactoryBean(JbpmConfiguration jbpmConfiguration) {
        this.jbpmConfiguration = jbpmConfiguration;
    }

    public JbpmContext getJbpmContext() {
        return jbpmConfiguration.createJbpmContext();
    }
}
