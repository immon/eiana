package org.iana.rzm.facade.admin.trans.notifications;

import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.rzm.facade.admin.trans.FacadeTransactionException;
import org.iana.rzm.facade.services.RZMStatefulService;
import org.iana.rzm.facade.system.notification.NotificationVO;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public interface AdminNotificationService extends RZMStatefulService {

    public List<NotificationVO> getNotifications(long transactionId) throws InfrastructureException;

    /**
     * Redends confirmation notifiactions of given <code>type</code> for the transaction identified
     * by given <code>transactionId</code> to addressees, who did not confirmed yet.
     * Notification body content is preceded with given <code>comment</comment>.
     * @param transactionId identifier of the transaction for which confirmation notifiactions
     * have to be resent.
     * @param type of the notifications to be resent.
     * @see NotificationVO.Type
     * @param comment to be added at the beginning of the notification body content.
     * @throws InfrastructureException when resending notification failed.
     */
    public void resendNotification(long transactionId, NotificationVO.Type type, String comment,  String email) throws InfrastructureException, FacadeTransactionException;

}
