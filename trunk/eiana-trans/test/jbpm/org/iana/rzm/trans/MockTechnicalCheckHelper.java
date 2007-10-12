package org.iana.rzm.trans;

import org.iana.notifications.NotificationManager;
import org.iana.notifications.NotificationSender;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.technicalcheck.CheckHelper;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Piotr Tkaczyk
 */
public class MockTechnicalCheckHelper implements CheckHelper {

    public boolean check(ExecutionContext executionContext, String period) {
        return true;
    }

    public boolean check(String period, TransactionData td, NotificationManager notificationManager, NotificationSender notificationSender, DomainManager domainManager, DiffConfiguration diffConfig, Long transactionId) {
        return true;
    }

    public boolean check(Domain domain, String submitterEmail, String period, NotificationManager notificationManager, NotificationSender notificationSender, Long transactionId) {
        return true;
    }

    public boolean check(Domain domain, String submitterEmail, NotificationManager notificationManager, NotificationSender notificationSender) {
        return true;
    }

    public boolean check(Domain domain, NotificationManager notificationManager, NotificationSender notificationSender) {
        return true;
    }
}
