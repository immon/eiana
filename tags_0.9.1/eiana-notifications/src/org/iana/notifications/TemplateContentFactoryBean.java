package org.iana.notifications;

import org.iana.rzm.common.validators.CheckTool;

import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class TemplateContentFactoryBean implements ContentFactory {

    protected ContentConverter templateConverter;

    public TemplateContentFactoryBean(ContentConverter templateConverter) {
        CheckTool.checkNull(templateConverter, "template converter cannot be null");
        this.templateConverter = templateConverter;
    }

    public Content createContent(String name) {
        return createContent(name, null);
    }

    public Content createContent(String name, Map<String, String> values) {
        TemplateContent tc = new TemplateContent();
        tc.setTemplateName(name);
        tc.setValues(values);
        tc.setTemplateConverter(templateConverter);
        return tc;
    }
}
