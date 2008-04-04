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

    public String toString() {
        StringBuffer buf = new StringBuffer();
        buf.append("from: ").append(from).append("\n\n");
        buf.append("subject: ").append(subject).append("\n\n");
        buf.append("body: ").append(body).append("\n\n");
        buf.append("data: ").append(data).append("\n");
        return buf.toString();
    }
}
