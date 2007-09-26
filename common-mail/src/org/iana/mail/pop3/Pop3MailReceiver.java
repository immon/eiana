package org.iana.mail.pop3;

import org.iana.mail.MailReceiver;
import org.iana.mail.MailReceiverException;
import org.iana.config.ConfigDAO;
import org.iana.config.Config;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;

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
    private Config config;


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

    public void setConfigDAO(ConfigDAO dao) throws ConfigException {
        config = new OwnedConfig(dao).getSubConfig(getClass().getSimpleName());
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

    private String getProtocol() throws ConfigException {
        return isSsl() ? "pop3s" : "pop3";
    }

    private boolean isSsl() throws ConfigException {
        if (config != null) {
            Boolean param = config.getBooleanParameter("ssl");
            if (param != null) return param;
        }
        return ssl;
    }

    private String getHost() throws ConfigException {
        if (config != null) {
            String param = config.getParameter("host");
            if (param != null) return param;
        }
        return host;
    }

    private String getUser() throws ConfigException {
        if (config != null) {
            String param = config.getParameter("user");
            if (param != null) return param;
        }
        return user;
    }

    private String getPassword() throws ConfigException {
        if (config != null) {
            String param = config.getParameter("password");
            if (param != null) return param;
        }
        return password;
    }

    private String getSubjectToken() throws ConfigException {
        if (config != null) {
            String param = config.getParameter("subjectToken");
            if (param != null) return param;
        }
        return subjectToken;
    }

    private Integer getPort() throws ConfigException {
        if (config != null) {
            Integer param = config.getIntegerParameter("port");
            if (param != null) return param;
        }
        return port;
    }

    public boolean isDebug() throws ConfigException {
        if (config != null) {
            Boolean param = config.getBooleanParameter("debug");
            if (param != null) return param;
        }
        return debug;
    }

    public List<MimeMessage> getMessages() throws MailReceiverException {
        List<MimeMessage> list = new ArrayList<MimeMessage>();
        try {
            Properties props = System.getProperties();
            if (getPort() != null) props.setProperty("mail." + getProtocol() + ".port", getPort().toString());
            Session session = Session.getInstance(props, null);
            session.setDebug(isDebug());
            Store store = session.getStore(getProtocol());
            if (getPort() != null)
                store.connect(getHost(), getPort(), getUser(), getPassword());
            else
                store.connect(getHost(), getUser(), getPassword());
            Folder folder = store.getFolder("inbox");
            if (folder == null || !folder.exists()) {
                throw new MailReceiverException("folder inbox does not exist");
            }
            folder.open(Folder.READ_WRITE);
            Message[] msgs = folder.getMessages();
            for (Message msg : msgs) {
                msg.setFlag(Flags.Flag.DELETED, true);
                if (msg.getSubject().contains(getSubjectToken())) {
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

    private String removeSubjectToken(String subject) throws ConfigException {
        int i = subject.indexOf(getSubjectToken());
        StringBuffer sb = new StringBuffer();
        sb.append(subject.substring(0, i)).append(subject.substring(i + getSubjectToken().length()));
        return sb.toString();
    }
}
