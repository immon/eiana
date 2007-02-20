package org.iana.notifications;

/**
 * This class represents a simple text content containing a text-subject and a text-body.
 *
 * @author Patrycja Wegrzynowicz
 */
public class TextContent implements Content {

    String subject;
    String body;

    public TextContent() {
    }

    public TextContent(String subject, String body) {
        this.subject = subject;
        this.body = body;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }
}
