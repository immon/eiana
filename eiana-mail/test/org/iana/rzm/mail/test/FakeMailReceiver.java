package org.iana.rzm.mail.test;

import org.iana.mail.MailReceiver;
import org.iana.mail.MailReceiverException;

import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public class FakeMailReceiver implements MailReceiver {
    public List<MimeMessage> getMessages() throws MailReceiverException {
        try {
            List<MimeMessage> result = new ArrayList<MimeMessage>();
            result.add(getMessage("ac@no-mail.org"));
            result.add(getMessage("tc@no-mail.org"));
            return result;
        } catch (MessagingException e) {
            throw new MailReceiverException(e);
        }
    }

    private MimeMessage getMessage(String from) throws MessagingException {
        MimeMessage message = new MimeMessage((Session) null);
        message.setFrom(new InternetAddress(from));
        message.setSubject("0 | PENDING_CONTACT_CONFIRMATION");
        message.setText("I ACCEPT");
        return message;
    }
}
