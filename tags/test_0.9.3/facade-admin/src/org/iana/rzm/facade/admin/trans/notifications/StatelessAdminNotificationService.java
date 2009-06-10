package org.iana.rzm.facade.admin.trans.notifications;

import org.iana.rzm.facade.system.notification.NotificationVO;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.admin.trans.FacadeTransactionException;
import org.iana.rzm.common.exceptions.InfrastructureException;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public interface StatelessAdminNotificationService {

    public List<NotificationVO> getNotifications(long transactionId, AuthenticatedUser authUser) throws InfrastructureException;

    public void resendNotification(long transactionId, NotificationVO.Type type, String comment,  String email, AuthenticatedUser authUser) throws InfrastructureException, FacadeTransactionException;

}
