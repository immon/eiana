package org.iana.rzm.facade.admin.trans.notifications;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.admin.trans.FacadeTransactionException;
import org.iana.rzm.facade.services.AbstractRZMStatefulService;
import org.iana.rzm.facade.system.notification.NotificationVO;

import java.util.List;

/**
 * @author Jakub Laszkiewicz
 * @author Patrycja Wegrzynowicz
 */
public class AdminNotificationServiceImpl extends AbstractRZMStatefulService implements AdminNotificationService {

    StatelessAdminNotificationService statelessAdminNotificationService;

    public AdminNotificationServiceImpl(StatelessAdminNotificationService statelessAdminNotificationService) {
        this.statelessAdminNotificationService = statelessAdminNotificationService;
    }

    public List<NotificationVO> getNotifications(long transactionId) throws InfrastructureException {
        return statelessAdminNotificationService.getNotifications(transactionId, getAuthenticatedUser());
    }

    public void resendNotification(long transactionId, NotificationVO.Type type, String comment, String addresseeEmail) throws InfrastructureException, FacadeTransactionException {
        statelessAdminNotificationService.resendNotification(transactionId, type, comment, addresseeEmail, getAuthenticatedUser());
    }

}
