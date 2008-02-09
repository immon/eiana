package org.iana.rzm.mail.processor.simple.data;

/**
 * @author Patrycja Wegrzynowicz
 */
public class Message {

    private String from;

    private String subject;

    private String body;

    private MessageData data;

    public Message(String from, String subject, String body, MessageData data) {
        this.from = from;
        this.subject = subject;
        this.body = body;
        this.data = data;
    }

    public String getFrom() {
        return from;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public MessageData getData() {
        return data;
    }

}
