package org.iana.rzm.mail.processor.simple.logger;

import org.iana.rzm.mail.processor.MailLogger;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.ticketing.TicketingService;
import org.iana.ticketing.TicketingException;
import org.apache.log4j.Logger;

/**
 * @author Patrycja Wegrzynowicz
 */
public class RTLogger implements MailLogger {

    private static Logger logger = Logger.getLogger(RTLogger.class);

    private static String FROM = "From: ";

    private static String SUBJECT = "Subject: ";

    private static String BODY = "Content: ";

    private TicketingService ticketingService;

    public RTLogger(TicketingService ticketingService) {
        CheckTool.checkNull(ticketingService, "ticketing service");
        this.ticketingService = ticketingService;
    }

    public void logMail(long ticketID, String from, String subject, String body) {
        try {
            StringBuffer comment = new StringBuffer();
            append(comment, FROM, from);
            append(comment, SUBJECT, subject);
            append(comment, BODY, body);
            ticketingService.addComment(ticketID, comment.toString());
        } catch (TicketingException e) {
            logger.error("logMail", e);
        }
    }

    private void append(StringBuffer comment, String label, String val) {
        comment.append(label).append(val).append("\n\n");
    }
}
