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
    private String subjectToken;
    private Integer port;
    private boolean ssl;
    private boolean debug;


    public Pop3MailReceiver(String host, String user, String password, String subjectToken) {
        this(host, null, user, password, subjectToken, false, false);
    }

    public Pop3MailReceiver(String host, String user, String password,
                            String subjectToken, boolean ssl) {
        this(host, null, user, password, subjectToken, ssl, false);
    }

    public Pop3MailReceiver(String host, Integer port, String user, String password,
                            String subjectToken, boolean ssl) {
        this(host, port, user, password, subjectToken, ssl, false);
    }

    public Pop3MailReceiver(String host, String user, String password,
                            String subjectToken, boolean ssl, boolean debug) {
        this(host, null, user, password, subjectToken, ssl, debug);
    }

    public Pop3MailReceiver(String host, Integer port, String user, String password,
                            String subjectToken, boolean ssl, boolean debug) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.subjectToken = subjectToken;
        this.port = port;
        this.ssl = ssl;
        this.debug = debug;
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void setSsl(boolean ssl) {
        this.ssl = ssl;
    }

    public void setPort(Integer port) {
        this.port = port;
    }

    private String getProtocol() {
        return ssl ? "pop3s" : "pop3";
    }

    public List<MimeMessage> getMessages() throws MailReceiverException {
        List<MimeMessage> list = new ArrayList<MimeMessage>();
        try {
            Properties props = System.getProperties();
            if (port != null) props.setProperty("mail." + getProtocol() + ".port", port.toString());
            Session session = Session.getInstance(props, null);
            session.setDebug(debug);
            Store store = session.getStore(getProtocol());
            if (port != null)
                store.connect(host, port, user, password);
            else
                store.connect(host, user, password);
            Folder folder = store.getFolder("inbox");
            if (folder == null || !folder.exists()) {
                throw new MailReceiverException("folder inbox does not exist");
            }
            folder.open(Folder.READ_WRITE);
            Message[] msgs = folder.getMessages();
            for (Message msg : msgs) { 
                msg.setFlag(Flags.Flag.DELETED, true);
                if (msg.getSubject().contains(subjectToken)) {
//                    msg.setSubject(removeSubjectToken(msg.getSubject()));
                    list.add(new MimeMessage((MimeMessage) msg));
                }
            }
            folder.close(true);
        } catch (Exception e) {
            throw new MailReceiverException("while receiving message", e);
        }
        return list;
    }

    private String removeSubjectToken(String subject) {
        int i = subject.indexOf(subjectToken);
        StringBuffer sb = new StringBuffer();
        sb.append(subject.substring(0, i)).append(subject.substring(i + subjectToken.length()));
        return sb.toString();
    }
}
