package org.iana.notifications.refactored;

import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * @author Patrycja Wegrzynowicz
 */
@Embeddable
public class PContent {

    @Basic
    private String subject;

    @Basic
    @Column(length = 4096)
    private String body;

    protected PContent() {
    }

    public PContent(String subject, String body) {
        init(subject, body);
    }

    private void init(String subject, String body) {
        CheckTool.checkNull(subject, "subject");
        CheckTool.checkNull(body, "body");
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PContent pContent = (PContent) o;

        if (body != null ? !body.equals(pContent.body) : pContent.body != null) return false;
        if (subject != null ? !subject.equals(pContent.subject) : pContent.subject != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (subject != null ? subject.hashCode() : 0);
        result = 31 * result + (body != null ? body.hashCode() : 0);
        return result;
    }
}
