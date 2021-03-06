package org.iana.rzm.mail.processor.simple;

import org.apache.log4j.Logger;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.MailsProcessor;
import org.iana.rzm.mail.processor.MailsProcessorException;
import org.iana.rzm.mail.processor.simple.data.Message;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.error.EmailErrorHandler;
import org.iana.rzm.mail.processor.simple.parser.EmailParser;
import org.iana.rzm.mail.processor.simple.processor.EmailProcessor;

import javax.mail.BodyPart;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.io.IOException;

/**
 * This is a simple implementation of MailsProcessor that encapsulates usage of EmailParser and EmailProcesses implementations.
 *
 * @author Patrycja Wegrzynowicz
 */
public class SimpleEmailsProcessor implements MailsProcessor {

    static Logger logger = Logger.getLogger(SimpleEmailsProcessor.class);

    private EmailParser parser;

    private EmailProcessor processor;

    private EmailErrorHandler error;


    public SimpleEmailsProcessor(EmailParser parser, EmailProcessor processor, EmailErrorHandler error) {
        CheckTool.checkNull(parser, "email parser");
        CheckTool.checkNull(processor, "email processor");
        CheckTool.checkNull(error, "email error handler");
        this.parser = parser;
        this.processor = processor;
        this.error = error;
    }

    public void process(String from, String subject, String content) throws MailsProcessorException {
        try {
            MessageData data = parser.parse(from, subject, content);
            processor.process(new Message(from, subject, content, data));
        } catch (Exception e) {
            error(from, subject, content, new Exception("Unexptected exception.", e));
        }
    }

    public void process(MimeMessage message) throws MailsProcessorException {
        if (message == null) {
            log("Cannot process null mime message.");
            return;
        }

        try {
            // re-used code written by jlaszk (see MailsProcessorBean)
            InternetAddress from = new InternetAddress("" + message.getFrom()[0], false);
            String subject = message.getSubject();
            Object content = message.getContent();
            if (content instanceof String) {
                String stringContent = (String) content;
                process(from.getAddress(), subject, stringContent);
                return;
            }

            if (content instanceof MimeMultipart) {
               if(processMultipart(from.getAddress(), subject, (MimeMultipart) content))
                   return;
            }

            error(from.getAddress(), subject, null, new Exception("Not supported message format. Please send plain text message."));

        } catch (MessagingException e) {
            log(e);
        } catch (IOException e) {
            log(e);
        }
    }

    private boolean processMultipart(String adress, String subject, MimeMultipart content) throws MailsProcessorException, MessagingException, IOException {
        for (int i=0; i < content.getCount(); i++) {
            BodyPart bp = content.getBodyPart(i);
            Object bpContent = bp.getContent();
            if (bpContent instanceof String) {
                process(adress, subject, (String) bpContent);
                return true;
            }
        }
        return false;
    }

    private void log(String msg) {
        logger.error(msg);
    }

    private void log(Exception e) {
        logger.error(e.getMessage(), e);
    }

    private void error(String from, String subject, String content, Exception e) {
        logger.error(e);
        error.error(from, subject, content, e);
    }

}


