package org.iana.rzm.trans.notifications;

import org.iana.notifications.Addressee;
import org.iana.notifications.Notification;
import org.iana.notifications.exception.NotificationException;

/**
 * @author Patrycja Wegrzynowicz
 */
public class Notification2Comment {

    private Notification notification;

    private static final String TO = "To: ";

    private static final String SUBJECT = "Subject: ";

    public Notification2Comment(Notification notification) {
        this.notification = notification;
    }

    public String getComment() throws NotificationException {
        StringBuffer buf = new StringBuffer();
        buf.append(createToLine()).append("\n");
        buf.append(createSubjectLine()).append("\n\n");
        buf.append(createContent());
        return buf.toString();
    }

    private StringBuffer createToLine() {
        StringBuffer ret = new StringBuffer(TO);
        int cur = 0;
        int size = notification.getAddressee().size();
        for (Addressee addr : notification.getAddressee()) {
            ret.append(addr.getEmail());
            if (++cur < size) ret.append(", ");
        }
        return ret;
    }

    private StringBuffer createSubjectLine() throws NotificationException {
        StringBuffer ret = new StringBuffer(SUBJECT);
        ret.append(notification.getContent().getSubject());
        return ret;
    }

    private StringBuffer createContent() throws NotificationException {
        return new StringBuffer(notification.getContent().getBody());
    }
}
