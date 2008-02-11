package org.iana.rzm.trans;

import org.iana.notifications.template.factory.TemplateFactory;
import org.iana.notifications.template.Template;
import org.iana.notifications.template.simple.SimpleTemplate;
import org.iana.notifications.template.simple.DefaultStringTemplateAlgorithm;

/**
 * @author Patrycja Wegrzynowicz
 */
public class MockTemplateFactory implements TemplateFactory {

    public Template getTemplate(String name) {
        return new SimpleTemplate(name, name, new DefaultStringTemplateAlgorithm());
    }
}
