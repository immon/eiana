package org.iana.rzm.trans;

import org.iana.notifications.refactored.template.factory.TemplateFactory;
import org.iana.notifications.refactored.template.Template;
import org.iana.notifications.refactored.template.simple.SimpleTemplate;
import org.iana.notifications.refactored.template.simple.DefaultStringTemplateAlgorithm;
import org.hibernate.reflection.java.xml.XMLContext;

/**
 * @author Patrycja Wegrzynowicz
 */
public class MockTemplateFactory implements TemplateFactory {

    public Template getTemplate(String name) {
        return new SimpleTemplate(name, name, new DefaultStringTemplateAlgorithm());
    }
}
