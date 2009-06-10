package org.iana.notifications.template.factory;

import org.iana.notifications.template.Template;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface TemplateFactory {

    Template getTemplate(String name);
    
}
