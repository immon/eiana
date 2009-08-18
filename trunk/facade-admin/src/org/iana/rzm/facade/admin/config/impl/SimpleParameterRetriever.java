package org.iana.rzm.facade.admin.config.impl;

import org.iana.config.impl.ConfigException;
import org.iana.mail.pop3.Pop3MailReceiver;
import org.iana.notifications.email.EmailSender;
import org.iana.notifications.email.TemplateTypeDependentEmailSender;

import java.util.HashMap;
import java.util.Map;

/**
 * This is a simple way to retrieve default (configured in spring) values for the parameters.
 *
 * @author Patrycja Wegrzynowicz
 */
public class SimpleParameterRetriever implements ConfigParameterRetriever {

    private TemplateTypeDependentEmailSender multiSmtp;

    private Pop3MailReceiver pop3;

    public SimpleParameterRetriever(TemplateTypeDependentEmailSender multiSmtp, Pop3MailReceiver pop3) {
        this.multiSmtp = multiSmtp;
        this.pop3 = pop3;
    }

    public Map<String, String> getParameter(String name) throws ConfigException {
        if (ConfigParameterNames.SMTP.equals(name)) {
            return getSmtpValues();
        } else if (ConfigParameterNames.POP3_CLASS.equals(name)) {
            return getPop3Values();
        } 
        throw new IllegalArgumentException("unknown parameter name: " + name);
    }

    Map<String, String> getSmtpValues() throws ConfigException {

        Map<String, String> ret = new HashMap<String, String>();

        for (String senderName : multiSmtp.getEmailSenderNames()) {
            EmailSender sender = multiSmtp.getEmailSender(senderName);
            ret.putAll(getSenderParameters(senderName, sender));
        }

        return ret;
    }

    private Map<String, String> getSenderParameters(String senderName, EmailSender sender) throws ConfigException {
        Map<String, String> retMap = new HashMap<String, String>();
        retMap.put(senderName + "." + ConfigParameterNames.SMTP_MAILER, sender.getEmailMailer());
        retMap.put(senderName + "." + ConfigParameterNames.SMTP_HOST, sender.getEmailMailhost());
        Integer port = sender.getEmailMailhostPort();
        retMap.put(senderName + "." + ConfigParameterNames.SMTP_PORT, port == null ? null : port.toString());
        retMap.put(senderName + "." + ConfigParameterNames.SMTP_FROM_ADDRESS, sender.getEmailFromAddress());
        retMap.put(senderName + "." + ConfigParameterNames.SMTP_USER_NAME, sender.getEmailUserName());
        retMap.put(senderName + "." + ConfigParameterNames.SMTP_USER_PWD,  sender.getEmailUserPassword());
        retMap.put(senderName + "." + ConfigParameterNames.SMTP_USE_SSL,  String.valueOf(sender.isEmailUseSSL()));
        retMap.put(senderName + "." + ConfigParameterNames.SMTP_USE_TLS,  String.valueOf(sender.isEmailUseTLS()));
        retMap.put(senderName + "." + ConfigParameterNames.SMTP_FROM,  sender.getMailSmtpFrom());

        return retMap;
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
