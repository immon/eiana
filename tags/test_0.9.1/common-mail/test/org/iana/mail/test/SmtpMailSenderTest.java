package org.iana.mail.test;

import org.iana.mail.MailSender;
import org.iana.mail.MailSenderException;
import org.iana.mail.smtp.SmtpMailSender;
import org.testng.annotations.Test;

/**
 * @author Jakub Laszkiewicz
 */
@Test(groups = "excluded")
public class SmtpMailSenderTest {
    private static final String MAILER = "test-mailer";
    private static final String MAIL_HOST = "test-host";
    private static final Integer MAIL_HOST_PORT = 587;
    private static final String MAIL_USER = "test-user";
    private static final String MAIL_PASSWORD = "test-password";
    private static final String MAIL_FROM = "from@test";
    private static final String MAIL_TO = "to@test";

    public void testSendMessage() throws MailSenderException {
        MailSender sender = new SmtpMailSender(MAILER, MAIL_HOST, MAIL_USER, MAIL_PASSWORD);
        sender.sendMail(MAIL_FROM, MAIL_TO, "test message", "test body");
    }

    public void testSendMessagePort() throws MailSenderException {
        MailSender sender = new SmtpMailSender(MAILER, MAIL_HOST, MAIL_HOST_PORT, MAIL_USER, MAIL_PASSWORD);
        sender.sendMail(MAIL_FROM, MAIL_TO, "test message port", "test body port");
    }

    public void testSendMessageSSL() throws MailSenderException {
        MailSender sender = new SmtpMailSender(MAILER, MAIL_HOST, MAIL_USER, MAIL_PASSWORD, true, false, true);
        sender.sendMail(MAIL_FROM, MAIL_TO, "test message ssl", "test body ssl");
    }

    public void testSendMessagePortSSL() throws MailSenderException {
        MailSender sender = new SmtpMailSender(MAILER, MAIL_HOST, MAIL_HOST_PORT, MAIL_USER, MAIL_PASSWORD, true, false, true);
        sender.sendMail(MAIL_FROM, MAIL_TO, "test message port ssl", "test body port ssl");
    }

    public void testSendMessageTLS() throws MailSenderException {
        MailSender sender = new SmtpMailSender(MAILER, MAIL_HOST, MAIL_USER, MAIL_PASSWORD, false, true, true);
        sender.sendMail(MAIL_FROM, MAIL_TO, "test message tls", "test body tls");
    }

    public void testSendMessagePortTLS() throws MailSenderException {
        MailSender sender = new SmtpMailSender(MAILER, MAIL_HOST, MAIL_HOST_PORT, MAIL_USER, MAIL_PASSWORD, false, true, true);
        sender.sendMail(MAIL_FROM, MAIL_TO, "test message port tls", "test body port tls");
    }

    public void testSendMessageSSLTLS() throws MailSenderException {
        MailSender sender = new SmtpMailSender(MAILER, MAIL_HOST, MAIL_USER, MAIL_PASSWORD, true, true, true);
        sender.sendMail(MAIL_FROM, MAIL_TO, "test message ssl/tls", "test body ssl/tls");
    }

    public void testSendMessagePortSSLTLS() throws MailSenderException {
        MailSender sender = new SmtpMailSender(MAILER, MAIL_HOST, MAIL_HOST_PORT, MAIL_USER, MAIL_PASSWORD, true, true, true);
        sender.sendMail(MAIL_FROM, MAIL_TO, "test message port ssl/tls", "test body port ssl/tls");
    }
}
