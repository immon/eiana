package org.iana.notifications;

import org.hibernate.annotations.CollectionOfElements;
import org.iana.notifications.exception.NotificationException;

import javax.persistence.*;
import java.util.Map;

/**
 * This class represents content based on a text template instantiated with a set of string parameters.
 *
 * @author Patrycja Wegrzynowicz
 * @author Piotr    Tkaczyk
 */

@Entity
public class TemplateContent extends Content {

    @Basic
    private String templateName;

    @CollectionOfElements
    @JoinTable(name = "TemplateContent_values")
    @Column(length = 4096)
    private Map<String, String> values;

    @Transient
    private String subject = null;
    @Transient
    private String body = null;

    protected TemplateContent() {
    }

    public TemplateContent(String templateName) {
        this.templateName = templateName;
    }

    public TemplateContent(String templateName, Map<String, String> values) {
        this.templateName = templateName;
        this.values = values;
    }

    public String getTemplateName() {
        return templateName;
    }

    public void setTemplateName(String templateName) {
        this.templateName = templateName;
    }

    public Map<String, String> getValues() {
        return values;
    }

    public void setValues(Map<String, String> values) {
        this.values = values;
    }

    public String getSubject() throws NotificationException {
        if (subject == null) subject = getContentConverter().createSubject(this);
        return subject;
    }

    public String getBody() throws NotificationException {
        if (body == null) body = getContentConverter().createBody(this);
        return body;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TemplateContent tc = (TemplateContent) o;

        if (!templateName.equals(tc.getTemplateName())) return false;
        if (!values.equals(tc.getValues())) return false;

        return true;
    }

    private ContentConverter getContentConverter() {
        return new TemplateContentConverter();
    }

    public boolean isTemplateContent() {
        return true;
    }
}
