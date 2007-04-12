package org.iana.notifications;

import org.hibernate.annotations.CollectionOfElements;

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
    @JoinTable(name="TemplateContent_values")
    private Map<String, String> values;

    protected TemplateContent() {}

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


    public String getSubject() {
        return null;
    }

    public String getBody() {
        return null;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TemplateContent tc = (TemplateContent) o;

        if (!templateName.equals(tc.getTemplateName())) return false;
        if (!values.equals(tc.getValues())) return false;

        return true;
    }
}
