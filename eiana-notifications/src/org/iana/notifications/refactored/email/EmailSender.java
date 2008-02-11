package org.iana.notifications.refactored.email;

import org.apache.log4j.Logger;
import org.iana.config.Config;
import org.iana.config.ParameterManager;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;
import org.iana.mail.MailSender;
import org.iana.mail.MailSenderException;
import org.iana.mail.smtp.SmtpMailSender;
import org.iana.notifications.refactored.NotificationSender;
import org.iana.notifications.refactored.PAddressee;
import org.iana.notifications.refactored.PContent;
import org.iana.notifications.refactored.PNotification;
import org.iana.rzm.common.validators.CheckTool;

import java.util.Set;

/**
 * @author Piotr Tkaczyk
 * @author Patrycja Wegrzynowicz
 */
public class EmailSender implements NotificationSender {

    private String emailMailhost;
    private Integer emailMailhostPort;
    private String emailMailer;
    private String emailFromAddress;
    private String emailUserName;
    private String emailUserPassword;
    private boolean emailUseSSL;
    private boolean emailUseTLS;
    private String mailSmtpFrom;
    private Config config;

    private static Logger logger = Logger.getLogger(EmailSender.class);

    public EmailSender(String mailHost, String mailer, String fromAddress,
                                  String userName, String password) {
        this(mailHost, null, mailer, fromAddress, userName, password, false, false);
    }

    public EmailSender(String mailHost, String mailer, String fromAddress,
                                  String userName, String password, boolean useSSL, boolean useTLS) {
        this(mailHost, null, mailer, fromAddress, userName, password, useSSL, useTLS);
    }

    public EmailSender(String mailHost, Integer mailHostPort, String mailer, String fromAddress,
                                  String userName, String password, boolean useSSL, boolean useTLS) {
        CheckTool.checkEmpty(mailHost, "mailHost param is null or empty");
        CheckTool.checkEmpty(mailer, "mailer param is null or empty");
        CheckTool.checkEmpty(fromAddress, "fromAddress param is null or empty");
        this.emailMailhost = mailHost;
        this.emailMailhostPort = mailHostPort;
        this.emailMailer = mailer;
        this.emailFromAddress = fromAddress;
        this.emailUserName = (userName == null || userName.trim().length() == 0) ? null : userName;
        this.emailUserPassword = (password == null || password.trim().length() == 0) ? null : password;
        this.emailUseSSL = useSSL;
        this.emailUseTLS = useTLS;
    }

    public void setConfig(ParameterManager manager) throws ConfigException {
        config = new OwnedConfig(manager).getSubConfig(getClass().getSimpleName());
    }

    public void setPort(Integer emailMailhostPort) {
        this.emailMailhostPort = emailMailhostPort;
    }

    public void setTls(boolean emailUseTLS) {
        this.emailUseTLS = emailUseTLS;
    }

    public void setSsl(boolean emailUseSSL) {
        this.emailUseSSL = emailUseSSL;
    }

    public void setMailSmtpFrom(String value) {
        mailSmtpFrom = value;
    }

    public String getEmailMailer() throws ConfigException {
        if (config != null) {
            String param = config.getParameter("emailMailer");
            if (param != null) {
                return param;
            }
        }
        return emailMailer;
    }

    public String getEmailMailhost() throws ConfigException {
        if (config != null) {
            String param = config.getParameter("emailMailhost");
            if (param != null) {
                return param;
            }
        }
        return emailMailhost;
    }

    public Integer getEmailMailhostPort() throws ConfigException {
        if (config != null) {
            Integer param = config.getIntegerParameter("emailMailhostPort");
            if (param != null) {
                return param;
            }
        }
        return emailMailhostPort;
    }

    public String getEmailFromAddress() throws ConfigException {
        if (config != null) {
            String param = config.getParameter("emailFromAddress");
            if (param != null) {
                return param;
            }
        }
        return emailFromAddress;
    }

    public String getEmailUserName() throws ConfigException {
        if (config != null) {
            String param = config.getParameter("emailUserName");
            if (param != null) {
                return param;
            }
        }
        return emailUserName;
    }

    public String getEmailUserPassword() throws ConfigException {
        if (config != null) {
            String param = config.getParameter("emailUserPassword");
            if (param != null) {
                return param;
            }
        }
        return emailUserPassword;
    }

    public boolean isEmailUseSSL() throws ConfigException {
        if (config != null) {
            Boolean param = config.getBooleanParameter("emailUseSSL");
            if (param != null) {
                return param;
            }
        }
        return emailUseSSL;
    }

    public boolean isEmailUseTLS() throws ConfigException {
        if (config != null) {
            Boolean param = config.getBooleanParameter("emailUseTLS");
            if (param != null) {
                return param;
            }
        }
        return emailUseTLS;
    }

    private void sendMail(String address, String subject, String body) throws EmailSenderException {
        try {
            MailSender mailer = new SmtpMailSender(getEmailMailer(), getEmailMailhost(), getEmailMailhostPort(), getEmailUserName(), getEmailUserPassword(), isEmailUseSSL(), isEmailUseTLS());
            String configParam = config.getParameter("mailSmtpFrom");
            ((SmtpMailSender) mailer).setMailSmtpFrom(configParam == null ? mailSmtpFrom : configParam);
            mailer.sendMail(getEmailFromAddress(), address, subject, body);
        } catch (MailSenderException e) {
            logger.warn("Unable to send notification to '" + address + "'. Reason: " + e.getMessage(), e);
            throw new EmailSenderException("Unable to send notification " + e.getMessage());
        } catch (ConfigException e) {
            throw new EmailSenderException(e);
        }

    }

    public void send(PNotification notification) throws EmailSenderException {
        CheckTool.checkNull(notification, "notification");
        CheckTool.checkCollectionNullOrEmpty(notification.getAddressees(), "notification addressees");
        CheckTool.checkNull(notification.getContent(), "notification content");

        Set<PAddressee> addressees = notification.getAddressees();
        PContent content = notification.getContent();
        StringBuilder address = new StringBuilder();
        for (PAddressee addr : addressees) {
            address.append(addr.getName());
            address.append("<");
            address.append(addr.getEmail());
            address.append(">");
            address.append(",");
        }
        // there always is at least one addressee 
        address.deleteCharAt(address.length()-1);
        sendMail(address.toString(), content.getSubject(), content.getBody());
    }

}
