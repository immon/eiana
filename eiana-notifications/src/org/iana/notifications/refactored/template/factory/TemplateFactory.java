package org.iana.notifications.refactored.template.factory;

import org.iana.notifications.refactored.template.Template;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface TemplateFactory {

    Template getTemplate(String name);
    
}
