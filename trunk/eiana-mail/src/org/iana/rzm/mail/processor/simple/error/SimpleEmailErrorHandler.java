package org.iana.rzm.mail.processor.simple.error;

import org.apache.log4j.Logger;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.notifications.refactored.NotificationSender;
import org.iana.notifications.refactored.PNotification;
import org.iana.notifications.refactored.PAddressee;
import org.iana.notifications.refactored.NotificationSenderException;

import java.util.StringTokenizer;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SimpleEmailErrorHandler implements EmailErrorHandler {

    private static Logger logger = Logger.getLogger(SimpleEmailErrorHandler.class);

    private static final String RESPONSE_PREFIX = "Re: ";

    private NotificationSender notifier;

    public SimpleEmailErrorHandler(NotificationSender notifier) {
        CheckTool.checkNull(notifier, "notification sender");
        this.notifier = notifier;
    }

    public void error(String to, String originalSubject, String originalContent, String message) {
        try {
            String subject = RESPONSE_PREFIX + originalSubject;
            String quotedContent = originalContent != null ? quote(originalContent) + "\n" : "";
            String content = quotedContent + message;
            PNotification notification = new PNotification(new PAddressee(to, to), subject, content);
            notifier.send(notification);
        } catch (NotificationSenderException e) {
            logger.error("Unexpected notifier exception", e);
        }
    }

    private String quote(String s) {
        StringTokenizer st = new StringTokenizer(s, "\n");
        StringBuilder result = new StringBuilder();
        while (st.hasMoreTokens()) {
            String line = st.nextToken();
            line = quoteLine(line);
            result.append(line).append("\n");
        }
        return result.toString();
    }

    private String quoteLine(String line) {
        if (line == null) return null;
        return "> " + line;
    }

    public void error(String to, String subject, String content, Exception e) {
        String msg = e.getMessage();
        if (msg == null) msg = "Exception " + e.getClass() + " occured.";
        error(to, subject, content, msg);
    }

}
