package org.iana.rzm.trans.technicalcheck;

import org.iana.notifications.NotificationManager;
import org.iana.notifications.NotificationSender;
import org.iana.objectdiff.DiffConfiguration;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.TransactionData;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author Piotr Tkaczyk
 */
public interface CheckHelper {

    public boolean check(ExecutionContext executionContext, String period);

    public boolean check(String period, TransactionData td, NotificationManager notificationManager,
                         NotificationSender notificationSender, DomainManager domainManager,
                         DiffConfiguration diffConfig, Long transactionId);

    public boolean check(Domain domain, String submitterEmail, String period, TransactionData td,
                         NotificationManager notificationManager, NotificationSender notificationSender,
                         Long transactionId);

    public boolean check(Domain domain, String submitterEmail,
                         NotificationManager notificationManager, NotificationSender notificationSender);

    public boolean check(Domain domain, NotificationManager notificationManager, NotificationSender notificationSender);
}
