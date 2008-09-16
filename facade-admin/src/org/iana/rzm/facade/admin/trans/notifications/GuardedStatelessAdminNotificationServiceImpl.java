package org.iana.rzm.facade.admin.trans.notifications;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.admin.trans.FacadeTransactionException;
import org.iana.rzm.facade.services.AbstractRZMStatelessService;
import org.iana.rzm.facade.auth.AuthenticatedUser;
import org.iana.rzm.facade.system.notification.NotificationVO;
import org.iana.rzm.user.UserManager;

import java.util.List;

/**
 * @author Piotr Tkaczyk
 */
public class GuardedStatelessAdminNotificationServiceImpl extends AbstractRZMStatelessService implements StatelessAdminNotificationService {

    StatelessAdminNotificationService statelessAdminNotificationService;

    public GuardedStatelessAdminNotificationServiceImpl(UserManager userManager, StatelessAdminNotificationService statelessAdminNotificationService) {
        super(userManager);
        this.statelessAdminNotificationService = statelessAdminNotificationService;
    }

    private void isUserInRole(AuthenticatedUser authUser) {
        isAdmin(authUser);
    }

    public List<NotificationVO> getNotifications(long transactionId, AuthenticatedUser authUser) throws InfrastructureException {
        isUserInRole(authUser);
        return statelessAdminNotificationService.getNotifications(transactionId, authUser);
    }

    public void resendNotification(long transactionId, NotificationVO.Type type, String comment, String addresseeEmail, AuthenticatedUser authUser) throws InfrastructureException, FacadeTransactionException {
        isUserInRole(authUser);
        statelessAdminNotificationService.resendNotification(transactionId, type, comment, addresseeEmail, authUser);
    }
}
