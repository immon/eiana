package org.iana.notifications;

import org.apache.log4j.Logger;
import org.iana.rzm.common.validators.CheckTool;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */
public class WhiteListNotificationSender implements NotificationSender {

    private Set<String> whiteList;

    private String defaultEmail;

    private NotificationSender notificationSender;

    private Logger logger = Logger.getLogger(WhiteListNotificationSender.class);

    public WhiteListNotificationSender(NotificationSender notificationSender) {
        CheckTool.checkNull(notificationSender, "notification sender");
        this.notificationSender = notificationSender;
    }

    public void send(PNotification notification) throws NotificationSenderException {
        Set<PAddressee> addressees = notification.getAddressees();
        for (PAddressee addressee : addressees) {
            if(!isOnWhiteList(addressee)) {
                logger.debug("Email addreessee not on white list (" + addressee.getEmail() + " replaced to " + defaultEmail + ")");
                addressee.setEmail(defaultEmail);
            }
        }
        
        notificationSender.send(notification);
    }

    public void setWhiteList(List<String> whiteList) {
        setWhiteList(new HashSet<String>(whiteList));
    }

    public void setWhiteList(Set<String> whiteList) {
        CheckTool.checkNull(whiteList, "emails white list");
        this.whiteList = whiteList;
    }

    public void setDefaultEmail(String defaultEmail) {
        this.defaultEmail = defaultEmail;
    }

    private boolean isOnWhiteList(PAddressee addressee) {
        return whiteList.contains(addressee.getEmail());
    }
}
