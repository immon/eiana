package org.iana.rzm.facade.admin.config.impl;

import org.iana.mail.pop3.Pop3MailReceiver;
import org.iana.notifications.email.EmailConstants;
import org.iana.notifications.email.EmailSender;
import org.iana.notifications.template.factory.DefaultTemplateFactory;
import org.iana.rzm.facade.auth.AuthenticationService;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface ConfigParameterNames {

    String SMTP_CLASS = EmailSender.class.getSimpleName();

    String SMTP_MAILER = SMTP_CLASS + "." + EmailConstants.SMTP_MAILER;

    String SMTP_HOST = SMTP_CLASS + "." + EmailConstants.SMTP_MAILHOST;

    String SMTP_PORT = SMTP_CLASS + "." + EmailConstants.SMTP_MAILHOST_PORT;

    String SMTP_FROM_ADDRESS = SMTP_CLASS + "." + EmailConstants.SMTP_FROM_ADDRESS;

    String SMTP_USER_NAME = SMTP_CLASS + "." + EmailConstants.SMTP_USER_NAME;

    String SMTP_USER_PWD = SMTP_CLASS + "." + EmailConstants.SMTP_USER_PWD;

    // string value of Boolean.TRUE or Boolean.FALSE
    String SMTP_USE_SSL = SMTP_CLASS + "." + EmailConstants.SMTP_USE_SSL;

    // string value of Boolean.TRUE or Boolean.FALSE
    String SMTP_USE_TLS = SMTP_CLASS + "." + EmailConstants.SMTP_USE_TLS;

    String SMTP_FROM = SMTP_CLASS + "." + EmailConstants.SMTP_SMTP_FROM;

    String POP3_CLASS = Pop3MailReceiver.class.getSimpleName();
    
    String POP3_HOST = POP3_CLASS + "." + Pop3MailReceiver.POP3_HOST;

    String POP3_PORT = POP3_CLASS + "." + Pop3MailReceiver.POP3_PORT;

    String POP3_USER_NAME = POP3_CLASS + "." + Pop3MailReceiver.POP3_USER;

    String POP3_USER_PWD = POP3_CLASS + "." + Pop3MailReceiver.POP3_PWD;

    // string value of Boolean.TRUE or Boolean.FALSE
    String POP3_USE_SSL = POP3_CLASS + "." + Pop3MailReceiver.POP3_SSL;

    String POP3_DEBUG = POP3_CLASS + "." + Pop3MailReceiver.POP3_DEBUG;

    String EMAIL_PGP_SIGNATURE_CLASS = DefaultTemplateFactory.class.getSimpleName();

    String EMAIL_PGP_PRIVATE_KEY = EMAIL_PGP_SIGNATURE_CLASS + "." + DefaultTemplateFactory.KEY;

    String EMAIL_PGP_PRIVATE_KEY_FILENAME = EMAIL_PGP_SIGNATURE_CLASS + "." + DefaultTemplateFactory.KEY_FILENAME;

    String EMAIL_PGP_PRIVATE_KEY_PASSPHRASE = EMAIL_PGP_SIGNATURE_CLASS + "." +  DefaultTemplateFactory.KEY_PASSPHRASE;

    String VERISIGN_PUBLIC_KEY = AuthenticationService.VERISIGN_PUBLIC_KEY;

    String VERISIGN_EMAIL = AuthenticationService.VERISIGN_EMAIL;

    String USDOC_PUBLIC_KEY = AuthenticationService.USDOC_PUBLIC_KEY;

    String USDOC_EMAIL = AuthenticationService.USDOC_EMAIL;

    String[] PARAMETERS = {
         SMTP_MAILER,
         SMTP_HOST,
         SMTP_PORT,
         SMTP_FROM_ADDRESS,
         SMTP_USER_NAME,
         SMTP_USER_PWD,
         SMTP_USE_SSL,
         SMTP_USE_TLS,
         SMTP_FROM,
         POP3_HOST,
         POP3_PORT,
         POP3_USER_NAME,
         POP3_USER_PWD,
         POP3_USE_SSL,
         POP3_DEBUG,
         EMAIL_PGP_PRIVATE_KEY,
         EMAIL_PGP_PRIVATE_KEY_FILENAME,
         EMAIL_PGP_PRIVATE_KEY_PASSPHRASE,
         VERISIGN_PUBLIC_KEY,
         VERISIGN_EMAIL,
         USDOC_PUBLIC_KEY,
         USDOC_EMAIL
    };

}
