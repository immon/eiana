package org.iana.mail.pop3;

import org.iana.mail.MailReceiver;
import org.iana.mail.MailReceiverException;

import javax.mail.internet.MimeMessage;
import javax.mail.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Properties;

/**
 * @author Jakub Laszkiewicz
 */
public class Pop3MailReceiver implements MailReceiver {
    private String host;
    private String user;
    private String password;

    public Pop3MailReceiver(String host, String user, String password) {
        this.host = host;
        this.user = user;
        this.password = password;
    }

    public List<MimeMessage> getMessages() throws MailReceiverException {
        List<MimeMessage> list = new ArrayList<MimeMessage>();
        try {
            Properties props = System.getProperties();
            Session session = Session.getInstance(props, null);
            Store store = session.getStore("pop3");
            store.connect(host, user, password);
            Folder folder = store.getFolder("inbox");
            if (folder == null || !folder.exists()) {
                throw new MailReceiverException("folder inbox does not exist");
            }
            folder.open(Folder.READ_WRITE);
            Message[] msgs = folder.getMessages();
            for (Message msg : msgs) {
                msg.setFlag(Flags.Flag.DELETED, true);
                list.add(new MimeMessage((MimeMessage) msg));
            }
            folder.close(true);
        } catch (Exception e) {
            throw new MailReceiverException("while receiving message", e);
        }
        return list;
    }
}
