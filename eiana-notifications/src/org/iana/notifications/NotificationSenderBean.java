package org.iana.notifications;

import org.apache.log4j.Logger;
import org.iana.mail.MailSender;
import org.iana.mail.MailSenderException;
import org.iana.mail.smtp.SmtpMailSender;
import org.iana.notifications.exception.NotificationException;
import org.iana.rzm.common.validators.CheckTool;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author Piotr Tkaczyk
 */

public class NotificationSenderBean implements NotificationSender {

    private String emailMailhost;
    private Integer emailMailhostPort;
    private String emailMailer;
    private String emailFromAddress;
    private String emailUserName;
    private String emailUserPassword;
    private boolean emailUseSSL;
    private boolean emailUseTLS;


    private static Logger logger = Logger.getLogger(NotificationSenderBean.class);

    public NotificationSenderBean(String mailHost, String mailer, String fromAddress,
                                  String userName, String password) {
        this(mailHost, null, mailer, fromAddress, userName, password, false, false);
    }

    public NotificationSenderBean(String mailHost, String mailer, String fromAddress,
                                  String userName, String password, boolean useSSL, boolean useTLS) {
        this(mailHost, null, mailer, fromAddress, userName, password, useSSL, useTLS);
    }

    public NotificationSenderBean(String mailHost, Integer mailHostPort, String mailer, String fromAddress,
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

    public void send(Addressee addressee, String subject, String body) throws NotificationException {
        CheckTool.checkNull(addressee, "null addressee param");
        sendMail(addressee.getName() + "<" + addressee.getEmail() + ">", subject, body);
    }

    public void send(Collection<Addressee> addressees, String subject, String body) throws NotificationException {
        CheckTool.checkCollectionNull(addressees, "null addressees list");
        StringBuffer address = new StringBuffer("");
        for (Iterator i = addressees.iterator(); i.hasNext();) {
            Addressee add = (Addressee) i.next();
            address.append(add.getName());
            address.append("<");
            address.append(add.getEmail());
            address.append(">");
            if (i.hasNext()) address.append(",");
        }
        sendMail(address.toString(), subject, body);
    }

    public void send(Addressee addressee, Content content) throws NotificationException {
        CheckTool.checkNull(addressee, "null addressee param");
        CheckTool.checkNull(content, "null notification content");
        sendMail(addressee.getName() + "<" + addressee.getEmail() + ">", content.getSubject(), content.getBody());
    }

    public void send(Collection<Addressee> addressees, Content content) throws NotificationException {
        CheckTool.checkCollectionNull(addressees, "null addressees list");
        CheckTool.checkNull(content, "null notification content");
        StringBuffer address = new StringBuffer("");
        for (Iterator i = addressees.iterator(); i.hasNext();) {
            Addressee add = (Addressee) i.next();
            address.append(add.getName());
            address.append("<");
            address.append(add.getEmail());
            address.append(">");
            if (i.hasNext()) address.append(",");
        }
        sendMail(address.toString(), content.getSubject(), content.getBody());
    }

    private void sendMail(String address, String subject, String body) throws NotificationException {
        try {
            MailSender mailer = new SmtpMailSender(emailMailer, emailMailhost, emailMailhostPort,
                    emailUserName, emailUserPassword, emailUseSSL, emailUseTLS);
            mailer.sendMail(emailFromAddress, address, subject, body);
        } catch (MailSenderException e) {
            logger.warn("Unable to send notification to '" + address + "'. Reason: " + e.getMessage());
            throw new NotificationException("Unable to send notification", e);
        }

    }
}
