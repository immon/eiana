package org.iana.notifications;

import javax.persistence.Entity;
import javax.persistence.Basic;

/**
 * This class represents a simple text content containing a text-subject and a text-body.
 *
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class TextContent extends Content {

    @Basic
    String subject;
    @Basic
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
