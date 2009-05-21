package org.iana.rzm.facade.admin.config.impl;

import org.iana.config.impl.ConfigException;
import org.iana.mail.pop3.Pop3MailReceiver;
import org.iana.notifications.email.EmailSender;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a simple way to retrieve default (configured in spring) values for the parameters.
 *
 * @author Patrycja Wegrzynowicz
 */
public class SimpleParameterRetriever implements ConfigParameterRetriever {

    private EmailSender smtp;

    private Pop3MailReceiver pop3;

    public SimpleParameterRetriever(EmailSender smtp, Pop3MailReceiver pop3) {
        this.smtp = smtp;
        this.pop3 = pop3;
    }

    public Map<String, String> getParameter(String name) throws ConfigException {
        if (ConfigParameterNames.SMTP_CLASS.equals(name)) {
            return getSmtpValues();
        } else if (ConfigParameterNames.POP3_CLASS.equals(name)) {
            return getPop3Values();
        } 
        throw new IllegalArgumentException("unknown parameter name: " + name);
    }

    Map<String, String> getSmtpValues() throws ConfigException {
        Map<String, String> ret = new HashMap<String, String>();
        ret.put(ConfigParameterNames.SMTP_MAILER, smtp.getEmailMailer());
        ret.put(ConfigParameterNames.SMTP_HOST, smtp.getEmailMailhost());
        Integer port = smtp.getEmailMailhostPort();
        ret.put(ConfigParameterNames.SMTP_PORT, port == null ? null : port.toString());
        ret.put(ConfigParameterNames.SMTP_FROM_ADDRESS, smtp.getEmailFromAddress());
        ret.put(ConfigParameterNames.SMTP_USER_NAME, smtp.getEmailUserName());
        ret.put(ConfigParameterNames.SMTP_USER_PWD,  smtp.getEmailUserPassword());
        ret.put(ConfigParameterNames.SMTP_USE_SSL,  String.valueOf(smtp.isEmailUseSSL()));
        ret.put(ConfigParameterNames.SMTP_USE_TLS,  String.valueOf(smtp.isEmailUseTLS()));
        ret.put(ConfigParameterNames.SMTP_FROM,  smtp.getMailSmtpFrom());
        return ret;
    }

    Map<String, String> getPop3Values() throws ConfigException {
        Map<String, String> ret = new HashMap<String, String>();
        ret.put(ConfigParameterNames.POP3_HOST, pop3.getHost());
        Integer port = pop3.getPort();
        ret.put(ConfigParameterNames.POP3_PORT, port == null ? null : port.toString());
        ret.put(ConfigParameterNames.POP3_USER_NAME, pop3.getUser());
        ret.put(ConfigParameterNames.POP3_USER_PWD,  pop3.getPassword());
        ret.put(ConfigParameterNames.POP3_USE_SSL,  String.valueOf(pop3.isSsl()));
        ret.put(ConfigParameterNames.POP3_DEBUG,  String.valueOf(pop3.isDebug()));
        return ret;
    }

}
