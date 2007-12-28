package org.iana.rzm.trans.notifications.default_producer;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.trans.notifications.producer.TemplateProducer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class DefaultTemplateProducer implements TemplateProducer {

    String template;

    public void setTemplate(String template) {
        CheckTool.checkEmpty(template, "template is empty or null");
        this.template = template;
    }

    public List<String> produce(Map dataSource) {
        return Arrays.asList(template);
    }
}
