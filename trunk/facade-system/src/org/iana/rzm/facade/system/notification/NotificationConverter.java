package org.iana.rzm.facade.system.notification;

import org.iana.notifications.Addressee;
import org.iana.notifications.Notification;
import org.iana.notifications.EmailAddressee;
import org.iana.notifications.exception.NotificationException;

import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

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
                notification.getContent().getSubject(), notification.getContent().getBody());
    }

    public static List<NotificationVO> toNotificationVOList(List<Notification> notificationList) throws NotificationException {
        List<NotificationVO> result = new ArrayList<NotificationVO>();
        for (Notification notification : notificationList)
            result.add(toNotificationVO(notification));
        return result;
    }
}
