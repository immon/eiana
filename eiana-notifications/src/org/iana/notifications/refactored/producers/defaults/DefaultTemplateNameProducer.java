package org.iana.notifications.refactored.producers.defaults;

import org.iana.notifications.refactored.producers.TemplateNameProducer;
import org.iana.rzm.common.validators.CheckTool;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class DefaultTemplateNameProducer implements TemplateNameProducer {

    String template;

    public DefaultTemplateNameProducer() {
    }

    public DefaultTemplateNameProducer(String template) {
        this.template = template;
    }

    public void setTemplate(String template) {
        CheckTool.checkEmpty(template, "template is empty or null");
        this.template = template;
    }

    public List<String> produce(Map dataSource) {
        return Arrays.asList(template);
    }
}
