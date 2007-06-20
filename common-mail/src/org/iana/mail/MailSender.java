package org.iana.mail;

import java.io.File;

/**
 * @author Jakub Laszkiewicz
 *
 * Enables e-mail sending.
 */
public interface MailSender {
    /**
     * Sends an e-mail.
     *
     * @param from message sender's e-mail address.
     * @param to message addressee e-mail eddress.
     * @param subject message subject.
     * @param body message body.
     * @throws MailSenderException
     */
    public void sendMail(String from, String to, String subject, String body) throws MailSenderException;

    /**
     * Sends an e-mail with an attachment. If the attachment is null the plain message is sent.
     *
     * @param from message sender's e-mail address.
     * @param to message addressee e-mail eddress.
     * @param subject message subject.
     * @param body message body.
     * @param attachment file to be attached to the sent message.
     * @throws MailSenderException
     */
    public void sendMail(String from, String to, String subject, String body, File attachment) throws MailSenderException;
}
