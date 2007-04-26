package org.iana.notifications;

import pl.nask.util.mail.Mailer;
import pl.nask.util.mail.MailerException;
import pl.nask.util.mail.smtp.SMTPMailer;

import java.util.Collection;
import java.util.Iterator;
import org.apache.log4j.Logger;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.notifications.exception.NotificationException;

/**
 * @author Piotr Tkaczyk
 */

public class NotificationSenderBean implements NotificationSender {

    private String EMAIL_MAILHOST;
    private String EMAIL_MAILER;
    private String EMAIL_FROMADDRESS;
    private String EMAIL_USERNAME;
    private String EMAIL_PASSWORD;

    private static Logger logger = Logger.getLogger(NotificationSenderBean.class);

    public NotificationSenderBean(String mailHost, String mailer, String fromAddress, String userName, String password) {
        CheckTool.checkEmpty(mailHost, "mailHost param is null or empty");
        CheckTool.checkEmpty(mailer, "mailer param is null or empty");
        CheckTool.checkEmpty(fromAddress, "fromAddress param is null or empty");
        this.EMAIL_MAILHOST    = mailHost;
        this.EMAIL_MAILER      = mailer;
        this.EMAIL_FROMADDRESS = fromAddress;
        this.EMAIL_USERNAME = (userName == null || userName.trim().length() == 0)? null : userName;
        this.EMAIL_PASSWORD = (password == null || password.trim().length() == 0)? null : password;
    }

    public void send(Addressee addressee, String subject, String body) throws NotificationException {
        CheckTool.checkNull(addressee, "null addressee param");
        sendMail(addressee.getName() + "<" + addressee.getEmail() + ">", subject, body);
    }

    public void send(Collection<Addressee> addressees, String subject, String body) throws NotificationException {
        CheckTool.checkCollectionNull(addressees, "null addressees list");
        StringBuffer address = new StringBuffer("");
        for(Iterator i = addressees.iterator();  i.hasNext();) {
            Addressee add = (Addressee)i.next();
            address.append(add.getName());
            address.append("<");address.append(add.getEmail()); address.append(">");
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
        for(Iterator i = addressees.iterator();  i.hasNext();) {
            Addressee add = (Addressee)i.next();
            address.append(add.getName());
            address.append("<");address.append(add.getEmail()); address.append(">");
            if (i.hasNext()) address.append(",");
        }
        sendMail(address.toString(), content.getSubject(), content.getBody());
    }

    private void sendMail(String address, String subject, String body) throws NotificationException {
        try {
            Mailer mailer =  new SMTPMailer(EMAIL_MAILHOST, EMAIL_MAILER, EMAIL_USERNAME, EMAIL_PASSWORD);
            mailer.sendMail(EMAIL_FROMADDRESS, address, subject, body);
        } catch (MailerException e) {
            logger.warn("Unable to send notification to '" + address + "'. Reason: " + e.getMessage());
            throw new NotificationException("Unable to send notification", e);
        }

    }
}
