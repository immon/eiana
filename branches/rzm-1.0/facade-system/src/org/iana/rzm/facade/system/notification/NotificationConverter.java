package org.iana.rzm.facade.system.notification;

import org.iana.notifications.PNotification;
import org.iana.notifications.PAddressee;

import java.util.*;

/**
 * @author Jakub Laszkiewicz
 */
public class NotificationConverter {
    public static NotificationAddresseeVO toNotificationAddresseeVO(PAddressee addressee) {
        return new NotificationAddresseeVO(addressee.getName(), addressee.getEmail());
    }

    public static Set<NotificationAddresseeVO> toNotificationAddresseeVOSet(Set<PAddressee> addresseeSet) {
        Set<NotificationAddresseeVO> result = new HashSet<NotificationAddresseeVO>();
        for (PAddressee addressee : addresseeSet)
            result.add(toNotificationAddresseeVO(addressee));
        return result;
    }

    public static NotificationVO toNotificationVO(PNotification notification) {
        return new NotificationVO(notification.getId(),
                toNotificationAddresseeVOSet(notification.getAddressees()),
                notification.getContent().getSubject(),
                notification.getContent().getBody(),
                notificationVOType.get(notification.getType()));
    }

    public static List<NotificationVO> toNotificationVOList(Collection<PNotification> notifications) {
        List<NotificationVO> result = new ArrayList<NotificationVO>();
        if (notifications != null) {
            for (PNotification notification : notifications) {
                result.add(toNotificationVO(notification));
            }
        }
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
        notificationVOType.put("usdoc-confirmation-nschange", NotificationVO.Type.USDOC_CONFIRMATION);
        notificationVOType.put("usdoc-confirmation-remainder", NotificationVO.Type.USDOC_CONFIRMATION_REMAINDER);
        notificationVOType.put("zone-insertion-alert", NotificationVO.Type.ZONE_INSERTION_ALERT);
        notificationVOType.put("zone-publication-alert", NotificationVO.Type.ZONE_PUBLICATION_ALERT);
        notificationVOType.put("completed", NotificationVO.Type.COMPLETED);
        notificationVOType.put("rejected", NotificationVO.Type.REJECTED);
        notificationVOType.put("withdrawn", NotificationVO.Type.WITHDRAWN);
        notificationVOType.put("admin-closed", NotificationVO.Type.ADMIN_CLOSED);
        notificationVOType.put("exception", NotificationVO.Type.EXCEPTION);
        notificationVOType.put("technical-check-period", NotificationVO.Type.TECHNICAL_CHECK_PERIOD);
        notificationVOType.put("technical-deficiencies", NotificationVO.Type.TECHNICAL_CHECK);
        notificationVOType.put("text", NotificationVO.Type.TEXT);

        for (Map.Entry<String, NotificationVO.Type> entry : notificationVOType.entrySet())
            notificationType.put(entry.getValue(), entry.getKey());
    }

    public static boolean isType(String ntype, NotificationVO.Type votype) {
        return notificationVOType.get(ntype) == votype;
    }

    public static String toNotificationType(NotificationVO.Type type) {
        return notificationType.get(type);
    }
}
