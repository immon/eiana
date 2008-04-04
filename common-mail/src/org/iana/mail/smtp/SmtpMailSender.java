package org.iana.mail.smtp;

import com.sun.mail.smtp.*;
import org.iana.mail.*;

import javax.activation.*;
import javax.mail.*;
import javax.mail.internet.*;
import java.io.*;
import java.util.*;

/**
 * @author Jakub Laszkiewicz
 * @author Piotr Tkaczyk
 */
public class SmtpMailSender implements MailSender {
    private String mailer;
    private String mailhost;
    private Integer port;
    private String userName;
    private String userPassword;
    private boolean ssl;
    private boolean tls;
    private boolean debug;
    private String mailSmtpFrom;

    public SmtpMailSender(String mailer, String mailhost, String userName, String userPassword) {
        this(mailer, mailhost, null, userName, userPassword, false, false, false);
    }

    public SmtpMailSender(String mailer, String mailhost, Integer port, String userName, String userPassword) {
        this(mailer, mailhost, port, userName, userPassword, false, false, false);
    }

    public SmtpMailSender(String mailer, String mailhost,
                          String userName, String userPassword, boolean ssl, boolean tls) {
        this(mailer, mailhost, null, userName, userPassword, ssl, tls, false);

    }

    public SmtpMailSender(String mailer, String mailhost,
                          String userName, String userPassword, boolean ssl, boolean tls,
                          boolean debug) {
        this(mailer, mailhost, null, userName, userPassword, ssl, tls, debug);
    }

    public SmtpMailSender(String mailer, String mailhost, Integer port,
                          String userName, String userPassword, boolean ssl, boolean tls) {
        this(mailer, mailhost, port, userName, userPassword, ssl, tls, false);        
    }

    public SmtpMailSender(String mailer, String mailhost, Integer port,
                          String userName, String userPassword, boolean ssl, boolean tls,
                          boolean debug) {
        this.mailer = mailer;
        this.mailhost = mailhost;
        this.port = port;
        this.userName = userName;
        this.userPassword = userPassword;
        this.ssl = ssl;
        this.tls = tls;
        this.debug = debug;
    }

    private String getProtocol() {
        return ssl ? "smtps" : "smtp";
    }

    private Session init() {
        Properties props = System.getProperties();
        props.put("mail." + getProtocol() + ".host", mailhost);

        if(mailSmtpFrom != null){
            props.put("mail.smtp.from", mailSmtpFrom);
        }

        if (port != null) props.put("mail." + getProtocol() + ".port", port.toString());
        if (userName != null || userPassword != null)
            props.put("mail." + getProtocol() + ".auth", "true");
        props.put("mail." + getProtocol() + ".starttls.enable", String.valueOf(tls));
        Session session = Session.getInstance(props);
        session.setDebug(debug);
        return session;
    }

    private MimeMessage createMessage(Session session,
                                      String from,
                                      String to,
                                      String cc,
                                      String subject) throws MessagingException {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(from));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to, true));

        if (cc != null && cc.trim().length() > 0) {
            message.addRecipients(Message.RecipientType.CC, InternetAddress.parse(cc, true));
        }

        message.setSubject(subject);
        message.setHeader("X-Mailer", mailer);
        message.setSentDate(new Date());
        return message;
    }

    private void sendMessage(Session session, Message message) throws MessagingException {
        SMTPTransport transport = (SMTPTransport) session.getTransport(getProtocol());
        try {
            if (userName != null || userPassword != null) {
                if (port != null)
                    transport.connect(mailhost, port, userName, userPassword);
                else
                    transport.connect(mailhost, userName, userPassword);
            } else
                transport.connect();
            transport.sendMessage(message, message.getAllRecipients());
        } finally {
            transport.close();
        }
    }

    public void setMailSmtpFrom(String value){
        mailSmtpFrom = value;
    }

    public void sendMail(String from, String to, String cc, String subject, String body) throws MailSenderException {
        Session session = init();
        try {
            MimeMessage message = createMessage(session, from, to, cc, subject);
            message.setText(body);
            sendMessage(session, message);
        } catch (MessagingException e) {
            throw new MailSenderException("Message sending failed.", e);
        }
    }

    public void sendMail(String from, String to, String cc, String subject, String body, File attachment) throws MailSenderException {
        if (attachment == null) {
            sendMail(from, to, cc, subject, body);
            return;
        }
        Session session = init();
        try {
            MimeMessage message = createMessage(session, cc, from, to, subject);
            MimeMultipart multipart = new MimeMultipart();
            if (body != null) {
                MimeBodyPart part = new MimeBodyPart();
                part.setText(body);
                multipart.addBodyPart(part);
            }
            MimeBodyPart part = new MimeBodyPart();
            part.setDataHandler(new DataHandler(new FileDataSource(attachment)));
            part.setFileName(attachment.getName());
            multipart.addBodyPart(part);
            message.setContent(multipart);
            sendMessage(session, message);
        } catch (MessagingException e) {
            throw new MailSenderException("Message sending failed.", e);
        }
    }
}
