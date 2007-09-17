package org.iana.rzm.facade.system.notification;

import org.iana.notifications.Addressee;
import org.iana.notifications.EmailAddressee;
import org.iana.notifications.Notification;
import org.iana.notifications.exception.NotificationException;

import java.util.*;

/**
 * @author Jakub Laszkiewicz
 */
public class NotificationConverter {
    public static NotificationAddresseeVO toNotificationAddresseeVO(Addressee addressee) {
        return new NotificationAddresseeVO(addressee.getName(), addressee.getEmail());
    }

    public static Set<NotificationAddresseeVO> toNotificationAddresseeVOSet(Set<Addressee> addresseeSet) {
        Set<NotificationAddresseeVO> result = new HashSet<NotificationAddresseeVO>();
        for (Addressee addressee : addresseeSet)
            result.add(toNotificationAddresseeVO(addressee));
        return result;
    }

    public static Addressee toAddressee(NotificationAddresseeVO addressee) {
        return new EmailAddressee(addressee.getName(), addressee.getEmail());
    }

    public static Set<Addressee> toAddresseeSet(Set<NotificationAddresseeVO> addresseeSet) {
        Set<Addressee> result = new HashSet<Addressee>();
        for (NotificationAddresseeVO addressee : addresseeSet)
            result.add(toAddressee(addressee));
        return result;
    }

    public static NotificationVO toNotificationVO(Notification notification) throws NotificationException {
        return new NotificationVO(notification.getObjId(), toNotificationAddresseeVOSet(notification.getAddressee()),
                notification.getContent().getSubject(), notification.getContent().getBody(),
                notificationVOType.get(notification.getType()));
    }

    public static List<NotificationVO> toNotificationVOList(List<Notification> notificationList) throws NotificationException {
        List<NotificationVO> result = new ArrayList<NotificationVO>();
        for (Notification notification : notificationList)
            result.add(toNotificationVO(notification));
         return result;
    }

    private static Map<String, NotificationVO.Type> notificationVOType = new HashMap<String, NotificationVO.Type>();
    private static Map<NotificationVO.Type, String> notificationType = new HashMap<NotificationVO.Type, String>();

    static {
        notificationVOType.put("contact-confirmation", NotificationVO.Type.CONTACT_CONFIRMATION);
        notificationVOType.put("contact-confirmation-remainder", NotificationVO.Type.CONTACT_CONFIRMATION_REMAINDER);
        notificationVOType.put("impacted_parties-confirmation", NotificationVO.Type.IMPACTED_PARTIES_CONFIRMATION);
        notificationVOType.put("impacted_parties-confirmation-remainder", NotificationVO.Type.IMPACTED_PARTIES_CONFIRMATION_REMAINDER);
        notificationVOType.put("usdoc-confirmation", NotificationVO.Type.USDOC_CONFIRMATION);
        notificationVOType.put("usdoc-confirmation-remainder", NotificationVO.Type.USDOC_CONFIRMATION_REMAINDER);
        notificationVOType.put("zone-insertion-alert", NotificationVO.Type.ZONE_INSERTION_ALERT);
        notificationVOType.put("zone-publication-alert", NotificationVO.Type.ZONE_PUBLICATION_ALERT);
        notificationVOType.put("completed", NotificationVO.Type.COMPLETED);
        notificationVOType.put("rejected", NotificationVO.Type.REJECTED);
        notificationVOType.put("withdrawn", NotificationVO.Type.WITHDRAWN);
        notificationVOType.put("admin-closed", NotificationVO.Type.ADMIN_CLOSED);
        notificationVOType.put("exception", NotificationVO.Type.EXCEPTION);
        notificationVOType.put("failed-technical-check-period", NotificationVO.Type.FAILED_TECHNICAL_CHECK_PERIOD);
        notificationVOType.put("failed-technical-check", NotificationVO.Type.FAILED_TECHNICAL_CHECK);
        notificationVOType.put("text", NotificationVO.Type.TEXT);

        for (Map.Entry<String, NotificationVO.Type> entry : notificationVOType.entrySet())
            notificationType.put(entry.getValue(), entry.getKey());
    }

    public static String toNotificationType(NotificationVO.Type type) {
        return notificationType.get(type);
    }
}
