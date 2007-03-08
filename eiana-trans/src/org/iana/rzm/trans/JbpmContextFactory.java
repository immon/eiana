package org.iana.rzm.trans;

import org.jbpm.JbpmContext;
import org.jbpm.JbpmConfiguration;

/**
 * @author Jakub Laszkiewicz
 */
 class JbpmContextFactory {
    public static JbpmContext getJbpmContext() {
        return JbpmConfiguration.getInstance("jbpm-config.xml").createJbpmContext();
    }
}
