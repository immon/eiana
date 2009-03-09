package org.iana.notifications.template.factory;

import org.iana.notifications.template.Template;
import org.iana.notifications.template.TemplateNotFoundException;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface TemplateFactory {

    Template getTemplate(String name) throws TemplateNotFoundException;
    
}
