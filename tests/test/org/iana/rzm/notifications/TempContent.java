package org.iana.rzm.notifications;

import org.iana.notifications.Content;

import javax.persistence.Entity;
import javax.persistence.Basic;

/**
 * @author Piotr Tkaczyk
 */

@Entity
public class TempContent extends Content {

    @Basic
    private String subject;
    @Basic
    private String body;


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
