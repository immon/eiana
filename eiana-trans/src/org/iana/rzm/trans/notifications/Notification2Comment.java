package org.iana.rzm.trans.notifications;

import org.iana.notifications.refactored.PAddressee;
import org.iana.notifications.refactored.PNotification;

/**
 * @author Patrycja Wegrzynowicz
 */
public class Notification2Comment {

    private PNotification notification;

    private static final String TO = "To: ";

    private static final String SUBJECT = "Subject: ";

    public Notification2Comment(PNotification notification) {
        this.notification = notification;
    }

    public String getComment() {
        StringBuffer buf = new StringBuffer();
        buf.append(createToLine()).append("\n");
        buf.append(createSubjectLine()).append("\n\n");
        buf.append(createContent());
        return buf.toString();
    }

    private StringBuffer createToLine() {
        StringBuffer ret = new StringBuffer(TO);
        int cur = 0;
        int size = notification.getAddressees().size();
        for (PAddressee addr : notification.getAddressees()) {
            ret.append(addr.getEmail());
            if (++cur < size) ret.append(", ");
        }
        return ret;
    }

    private StringBuffer createSubjectLine() {
        StringBuffer ret = new StringBuffer(SUBJECT);
        ret.append(notification.getContent().getSubject());
        return ret;
    }

    private StringBuffer createContent() {
        return new StringBuffer(notification.getContent().getBody());
    }
}
