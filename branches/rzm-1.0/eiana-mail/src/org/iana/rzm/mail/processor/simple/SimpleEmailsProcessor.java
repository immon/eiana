package org.iana.rzm.mail.processor.simple;

import org.apache.log4j.Logger;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.mail.processor.MailsProcessor;
import org.iana.rzm.mail.processor.MailsProcessorException;
import org.iana.rzm.mail.processor.simple.data.Message;
import org.iana.rzm.mail.processor.simple.data.MessageData;
import org.iana.rzm.mail.processor.simple.error.EmailErrorHandler;
import org.iana.rzm.mail.processor.simple.parser.EmailParseException;
import org.iana.rzm.mail.processor.simple.parser.EmailParser;
import org.iana.rzm.mail.processor.simple.parser.VerisignEmailParseException;
import org.iana.rzm.mail.processor.simple.processor.EmailProcessException;
import org.iana.rzm.mail.processor.simple.processor.EmailProcessor;

import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
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
        } catch (VerisignEmailParseException e) {
            log(e);
        } catch (EmailParseException e) {
            error(from, subject, content, e);
        } catch (EmailProcessException e) {
            error(from, subject, content, e);
        } catch (Exception e) {
            error(from, subject, content, "Unexptected exception.");
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
            } else {
                error(from.getAddress(), subject, null, "Not supported message format. Please send plain text message.");
            }
        } catch (MessagingException e) {
            log(e);
        } catch (IOException e) {
            log(e);
        }
    }

    private void log(String msg) {
        logger.error(msg);
    }

    private void log(Exception e) {
        logger.error(e.getMessage(), e);
    }

    private void error(String from, String subject, String content, Exception e) {
        log(e);
        error.error(from, subject, content, e);
    }

    private void error(String from, String subject, String content, String msg) {
        log(msg);
        error.error(from, subject, content, msg);
    }

}


