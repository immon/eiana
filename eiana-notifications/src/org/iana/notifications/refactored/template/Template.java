package org.iana.notifications.refactored.template;

import org.iana.notifications.refactored.PContent;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Template {

    PContent instantiate(Object data) throws TemplateInstantiationException ;

}
