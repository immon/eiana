package org.iana.notifications.email;

import org.apache.log4j.Logger;
import org.iana.config.Config;
import org.iana.config.ParameterManager;
import org.iana.config.impl.ConfigException;
import org.iana.config.impl.OwnedConfig;
import org.iana.mail.MailSender;
import org.iana.mail.MailSenderException;
import org.iana.mail.smtp.SmtpMailSender;
import org.iana.notifications.NotificationSender;
import org.iana.notifications.PAddressee;
import org.iana.notifications.PContent;
import org.iana.notifications.PNotification;
import org.iana.rzm.common.validators.CheckTool;

import java.util.Set;

/**
 * @author Piotr Tkaczyk
 * @author Patrycja Wegrzynowicz
 */
public class EmailSender implements NotificationSender, EmailConstants {

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

    private String subConfigName;

    private transient static Logger logger = Logger.getLogger(EmailSender.class);

    public EmailSender(String mailHost, String mailer, String fromAddress,
                                  String userName, String password, String subConfigName) {
        this(mailHost, null, mailer, fromAddress, userName, password, false, false, subConfigName);
    }

    public EmailSender(String mailHost, String mailer, String fromAddress,
                                  String userName, String password, boolean useSSL, boolean useTLS, String subConfigName) {
        this(mailHost, null, mailer, fromAddress, userName, password, useSSL, useTLS, subConfigName);
    }

    public EmailSender(String mailHost, Integer mailHostPort, String mailer, String fromAddress,
                                  String userName, String password, boolean useSSL, boolean useTLS, String subConfigName) {
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
        this.subConfigName = subConfigName;
    }

    public void setConfig(ParameterManager manager) throws ConfigException {
        if (subConfigName != null && subConfigName.trim().length() > 0)
            config = new OwnedConfig(manager).getSubConfig(subConfigName);
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

    public String getMailSmtpFrom() {
        return mailSmtpFrom;
    }

    public String getEmailMailer() throws ConfigException {
        if (config != null) {
            String param = config.getParameter(SMTP_MAILER);
            if (param != null) {
                return param;
            }
        }
        return emailMailer;
    }

    public String getEmailMailhost() throws ConfigException {
        if (config != null) {
            String param = config.getParameter(SMTP_MAILHOST);
            if (param != null) {
                return param;
            }
        }
        return emailMailhost;
    }

    public Integer getEmailMailhostPort() throws ConfigException {
        if (config != null) {
            Integer param = config.getIntegerParameter(SMTP_MAILHOST_PORT);
            if (param != null) {
                return param;
            }
        }
        return emailMailhostPort;
    }

    public String getEmailFromAddress() throws ConfigException {
        if (config != null) {
            String param = config.getParameter(SMTP_FROM_ADDRESS);
            if (param != null) {
                return param;
            }
        }
        return emailFromAddress;
    }

    public String getEmailUserName() throws ConfigException {
        if (config != null) {
            String param = config.getParameter(SMTP_USER_NAME);
            if (param != null) {
                return param;
            }
        }
        return emailUserName;
    }

    public String getEmailUserPassword() throws ConfigException {
        if (config != null) {
            String param = config.getParameter(SMTP_USER_PWD);
            if (param != null) {
                return param;
            }
        }
        return emailUserPassword;
    }

    public boolean isEmailUseSSL() throws ConfigException {
        if (config != null) {
            Boolean param = config.getBooleanParameter(SMTP_USE_SSL);
            if (param != null) {
                return param;
            }
        }
        return emailUseSSL;
    }

    public boolean isEmailUseTLS() throws ConfigException {
        if (config != null) {
            Boolean param = config.getBooleanParameter(SMTP_USE_TLS);
            if (param != null) {
                return param;
            }
        }
        return emailUseTLS;
    }

    private void sendMail(String address, String cc, String subject, String body) throws EmailSenderException {
        try {
            MailSender mailer = new SmtpMailSender(getEmailMailer(), getEmailMailhost(), getEmailMailhostPort(), getEmailUserName(), getEmailUserPassword(), isEmailUseSSL(), isEmailUseTLS());
            String configParam = config.getParameter(SMTP_SMTP_FROM);
            ((SmtpMailSender) mailer).setMailSmtpFrom(configParam == null ? mailSmtpFrom : configParam);
            mailer.sendMail(getEmailFromAddress(), address, cc, subject, body);
        } catch (MailSenderException e) {
            logger.warn("Unable to send notification to '" + address + "'. Reason: " + e.getMessage(), e);
            throw new EmailSenderException("Unable to send notification " + e.getMessage(), e);
        } catch (ConfigException e) {
            throw new EmailSenderException(e);
        }

    }

    public void send(PNotification notification) throws EmailSenderException {
        CheckTool.checkNull(notification, "notification");
        CheckTool.checkNull(notification.getAddressees(), "notification addressees");
        CheckTool.checkNull(notification.getContent(), "notification content");

        Set<PAddressee> addressees = notification.getAddressees();
        PContent content = notification.getContent();
        StringBuilder ccAddress = new StringBuilder();
        StringBuilder address = new StringBuilder();
        for (PAddressee addr : addressees) {
            if (addr.isCCEmailAddressee()) {
                ccAddress.append(addr.toEmailAddressForm());
            } else {
                address.append(addr.toEmailAddressForm());
            }

        }

        if (address.length() > 0) address.deleteCharAt(address.length()-1);
        if (ccAddress.length() > 0) ccAddress.deleteCharAt(ccAddress.length()-1);

        sendMail(address.toString(), ccAddress.toString(), content.getSubject(), content.getBody());
    }
}
