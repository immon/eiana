package org.iana.mail.pop3;

import org.iana.config.Config;
import org.iana.config.ParameterManager;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;
import org.iana.mail.MailReceiver;
import org.iana.mail.MailReceiverException;

import javax.mail.*;
import javax.mail.internet.MimeMessage;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * @author Jakub Laszkiewicz
 */
public class Pop3MailReceiver implements MailReceiver {
    private String host;
    private String user;
    private String password;
    private Integer port;
    private boolean ssl;
    private boolean debug;
    private Config config;

    public static final String POP3_HOST = "host";
    public static final String POP3_USER = "user";
    public static final String POP3_PWD = "password";
    public static final String POP3_SSL = "ssl";
    public static final String POP3_PORT = "port";
    public static final String POP3_DEBUG = "debug";


    public Pop3MailReceiver(String host, String user, String password) {
        this(host, null, user, password, false, false);
    }

    public Pop3MailReceiver(String host, String user, String password,
                            boolean ssl) {
        this(host, null, user, password, ssl, false);
    }

    public Pop3MailReceiver(String host, Integer port, String user, String password,
                            boolean ssl) {
        this(host, port, user, password, ssl, false);
    }

    public Pop3MailReceiver(String host, String user, String password,
                            boolean ssl, boolean debug) {
        this(host, null, user, password, ssl, debug);
    }

    public Pop3MailReceiver(String host, Integer port, String user, String password,
                            boolean ssl, boolean debug) {
        this.host = host;
        this.user = user;
        this.password = password;
        this.port = port;
        this.ssl = ssl;
        this.debug = debug;
    }

    public void setConfig(ParameterManager manager) throws ConfigException {
        config = new OwnedConfig(manager).getSubConfig(getClass().getSimpleName());
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
            Boolean param = config.getBooleanParameter(POP3_SSL);
            if (param != null) return param;
        }
        return ssl;
    }

    private String getHost() throws ConfigException {
        if (config != null) {
            String param = config.getParameter(POP3_HOST);
            if (param != null) return param;
        }
        return host;
    }

    private String getUser() throws ConfigException {
        if (config != null) {
            String param = config.getParameter(POP3_USER);
            if (param != null) return param;
        }
        return user;
    }

    private String getPassword() throws ConfigException {
        if (config != null) {
            String param = config.getParameter(POP3_PWD);
            if (param != null) return param;
        }
        return password;
    }

    private Integer getPort() throws ConfigException {
        if (config != null) {
            Integer param = config.getIntegerParameter(POP3_PORT);
            if (param != null) return param;
        }
        return port;
    }

    public boolean isDebug() throws ConfigException {
        if (config != null) {
            Boolean param = config.getBooleanParameter(POP3_DEBUG);
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
                list.add(new MimeMessage((MimeMessage) msg));
            }
            folder.close(true);
        } catch (Exception e) {
            throw new MailReceiverException("while receiving message", e);
        }
        return list;
    }

}
