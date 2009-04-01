package org.iana.rzm.facade.admin.config;

import org.iana.mail.pop3.Pop3MailReceiver;
import org.iana.notifications.email.EmailSender;
import org.iana.notifications.template.factory.DefaultTemplateFactory;
import org.iana.rzm.facade.auth.AuthenticationService;

/**
 * @author Patrycja Wegrzynowicz
 */
public abstract class ConfigParameterNames {

    private ConfigParameterNames() {}

    private static final String SMTP_CLASS = EmailSender.class.getSimpleName();

    public static final String SMTP_MAILER = SMTP_CLASS + "." + EmailSender.SMTP_MAILER;

    public static final String SMTP_HOST = SMTP_CLASS + "." + EmailSender.SMTP_MAILHOST;

    public static final String SMTP_PORT = SMTP_CLASS + "." + EmailSender.SMTP_MAILHOST_PORT;

    public static final String SMTP_FROM_ADDRESS = SMTP_CLASS + "." + EmailSender.SMTP_FROM_ADDRESS;

    public static final String SMTP_USER_NAME = SMTP_CLASS + "." + EmailSender.SMTP_USER_NAME;

    public static final String SMTP_USER_PWD = SMTP_CLASS + "." + EmailSender.SMTP_USER_PWD;

    // string value of Boolean.TRUE or Boolean.FALSE
    public static final String SMTP_USE_SSL = SMTP_CLASS + "." + EmailSender.SMTP_USE_SSL;

    // string value of Boolean.TRUE or Boolean.FALSE
    public static final String SMTP_USE_TLS = SMTP_CLASS + "." + EmailSender.SMTP_USE_TLS;

    public static final String SMTP_FROM = SMTP_CLASS + "." + EmailSender.SMTP_SMTP_FROM;

    private static final String POP3_CLASS = Pop3MailReceiver.class.getSimpleName();
    
    public static final String POP3_HOST = POP3_CLASS + "." + Pop3MailReceiver.POP3_HOST;

    public static final String POP3_PORT = POP3_CLASS + "." + Pop3MailReceiver.POP3_PORT;

    public static final String POP3_USER_NAME = POP3_CLASS + "." + Pop3MailReceiver.POP3_USER;

    public static final String POP3_USER_PWD = POP3_CLASS + "." + Pop3MailReceiver.POP3_PWD;

    // string value of Boolean.TRUE or Boolean.FALSE
    public static final String POP3_USE_SSL = POP3_CLASS + "." + Pop3MailReceiver.POP3_SSL;

    public static final String POP3_DEBUG = POP3_CLASS + "." + Pop3MailReceiver.POP3_DEBUG;

    private static final String EMAIL_PGP_SIGNATURE_CLASS = DefaultTemplateFactory.class.getSimpleName();

    public static final String EMAIL_PGP_PRIVATE_KEY = EMAIL_PGP_SIGNATURE_CLASS + "." + DefaultTemplateFactory.KEY;

    public static final String EMAIL_PGP_PRIVATE_KEY_FILENAME = EMAIL_PGP_SIGNATURE_CLASS + "." + DefaultTemplateFactory.KEY_FILENAME;

    public static final String EMAIL_PGP_PRIVATE_KEY_PASSPHRASE = EMAIL_PGP_SIGNATURE_CLASS + "." +  DefaultTemplateFactory.KEY_PASSPHRASE;


    public static final String VERISIGN_PUBLIC_KEY = AuthenticationService.VERISIGN_PUBLIC_KEY;

    public static final String VERISIGN_EMAIL = AuthenticationService.VERISIGN_EMAIL;

    public static final String USDOC_PUBLIC_KEY = AuthenticationService.USDOC_PUBLIC_KEY;

    public static final String USDOC_EMAIL = AuthenticationService.USDOC_EMAIL;
}
