package org.iana.rzm.facade.admin.trans;

import org.iana.rzm.facade.system.notification.NotificationVO;
import org.iana.rzm.facade.system.notification.NotificationAddresseeVO;
import org.iana.rzm.facade.services.RZMStatefulService;
import org.iana.rzm.common.exceptions.InfrastructureException;
import org.iana.criteria.Criterion;

import java.util.List;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public interface AdminNotificationService extends RZMStatefulService {

    public List<NotificationVO> getNotifications(long transactionId) throws InfrastructureException;

    /**
     * Gets notifications specified by given <code>criteria</code>. Field names which can be
     * used with <code>criteria</code> are specified in <code>NotificationCriteriaFields</code>
     * interface.
     * @see org.iana.criteria.Criterion
     * @see org.iana.notifications.NotificationCriteriaFields
     * @param criteria specify notifications to be returned.
     * @return notifications specified by given <code>criteria</code>.
     * @throws InfrastructureException when getting notifications failed.
     */
    public List<NotificationVO> getNotifications(Criterion criteria) throws InfrastructureException;

    /**
     * Redends any persisted notification identified by <code>notificationId</code>
     * to given <code>addressees<code>. Notification body content is preceded with given
     * <code>comment</comment>.
     * @param addressees specifies to whom the notification has to be sent.
     * @param notificationId identifier of the notification to be resent.
     * @param comment to be added at the beginning of the notification body content.
     * @throws InfrastructureException when resending notification failed.
     */
    public void resendNotification(Set<NotificationAddresseeVO> addressees, Long notificationId, String comment) throws InfrastructureException;

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
    public void resendNotification(Long transactionId, NotificationVO.Type type, String comment) throws InfrastructureException, FacadeTransactionException;

}
