package org.iana.notifications;

import java.util.Map;

/**
 * This class represents content based on a text template instantiated with a set of string parameters.
 *
 * @author Patrycja Wegrzynowicz
 */
public class TemplateContent implements Content {

    private String templateName;
    private Map<String, String> values;

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
}
