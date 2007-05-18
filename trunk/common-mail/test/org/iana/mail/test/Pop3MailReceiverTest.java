package org.iana.mail.test;

import org.iana.mail.MailReceiver;
import org.iana.mail.MailReceiverException;
import org.iana.mail.pop3.Pop3MailReceiver;
import org.testng.annotations.Test;

import javax.mail.Address;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 */
@Test (groups = {"excluded"})
public class Pop3MailReceiverTest {
    private static final String MAIL_HOST = "mail-host";
    private static final String MAIL_USER = "mail-user";
    private static final String MAIL_PASSWORD = "password";

    private static final String VALID_FROM = "jakub.laszkiewicz@nask.pl";
    private static final String VALID_SUBJECT = "Test";
    private static final String VALID_CONTENT = "Test content.";

    @Test
    public void testGetMessages() throws MailReceiverException, MessagingException, IOException {
        MailReceiver receiver = new Pop3MailReceiver(MAIL_HOST, MAIL_USER, MAIL_PASSWORD, "");
        List<MimeMessage> messages = receiver.getMessages();
        assert messages != null;
        assert messages.size() == 1;
        MimeMessage msg = messages.iterator().next();
        assert msg != null;

        Address [] froms = msg.getFrom();
        assert froms.length == 1;
        InternetAddress from = new InternetAddress("" + msg.getFrom()[0], false);
        assert VALID_FROM.equals(from.getAddress());

        assert VALID_SUBJECT.equals(msg.getSubject());

        assert msg.getContent() instanceof String;
        String content = (String) msg.getContent();
        assert VALID_CONTENT.equals(content);
    }
}
