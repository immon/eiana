package org.iana.mail;

import javax.mail.internet.MimeMessage;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
public interface MailReceiver {
    public List<MimeMessage> getMessages() throws MailReceiverException;
}
