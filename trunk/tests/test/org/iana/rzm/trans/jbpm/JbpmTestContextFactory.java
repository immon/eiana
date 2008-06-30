package org.iana.rzm.trans.jbpm;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;

/**
 * @author Jakub Laszkiewicz
 */
public class JbpmTestContextFactory {
    private static final String jbpmConfigurationXml =
            "<jbpm-configuration>" +
                    "  <jbpm-context>" +
                    "    <GuardedSystemTransactionService name='persistence' " +
                    "             factory='org.jbpm.persistence.db.DbPersistenceServiceFactory' />" +
                    "    <GuardedSystemTransactionService name='scheduler'" +
                    "             factory='org.jbpm.scheduler.db.DbSchedulerServiceFactory' />" +
                    "  </jbpm-context>" +
                    "  <string name='resource.hibernate.cfg.xml' " +
                    "          value='eiana-trans-hibernate.cfg.xml' />" +
                    "  <string name='resource.business.calendar' " +
                    "          value='org/jbpm/calendar/jbpm.business.calendar.properties' />" +
                    "  <string name='resource.default.modules' " +
                    "          value='org/jbpm/graph/def/jbpm.default.modules.properties' />" +
                    "  <string name='resource.converter' " +
                    "          value='org/jbpm/db/hibernate/jbpm.converter.properties' />" +
                    "  <string name='resource.action.types' " +
                    "          value='org/jbpm/graph/action/action.types.xml' />" +
                    "  <string name='resource.node.types' " +
                    "          value='org/jbpm/graph/node/node.types.xml' />" +
                    "  <string name='resource.varmapping' " +
                    "          value='eiana-trans.jbpm.varmapping.xml'/>" +
                    "</jbpm-configuration>";

    private static JbpmConfiguration jbpmConfiguration =
            JbpmConfiguration.parseXmlString(jbpmConfigurationXml);

    public static JbpmContext getJbpmContext() {
        return (jbpmConfiguration == null ? JbpmConfiguration.getInstance(jbpmConfigurationXml) : jbpmConfiguration).createJbpmContext();
    }
}
