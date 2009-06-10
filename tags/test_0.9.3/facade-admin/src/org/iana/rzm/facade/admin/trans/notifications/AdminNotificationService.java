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

    /**
     * Returns a list of persistent notifications for a given transaction.
     *
     * @param transactionId the transaction identifier
     * @return a list of persistent notifications; empty list if none notification has been found.
     *
     * @throws InfrastructureException when resending notification failed.
     */
    public List<NotificationVO> getNotifications(long transactionId) throws InfrastructureException;

    /**
     * Re-sends a notification of a given type already sent in a context of a given transaction.
     * The notification is re-sent only to those who have not confirmed it yet; an additional email addressee
     * can be specified. The subject and body of the notification remains the same but in case of a body it's possible to add
     * a comment which is added at a top of it.
     *
     * @param transactionId the identifier of the transaction in a context of which the notification to be re-sent was
     * produced.
     * @param type the type of the notification to re-sent.
     * @param comment the comment to be added at a top of the notification body; if null no comment is added and the body
     * of the resent notification is the same as the original one.
     * @param email an additional email to which the new notifiaction is to be sent as well (in addition to the addressees
     * which have not confirmed yet).
     *
     *
     * @throws InfrastructureException when resending notification failed.
     */
    public void resendNotification(long transactionId, NotificationVO.Type type, String comment,  String email) throws InfrastructureException, FacadeTransactionException;

}
