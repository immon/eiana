package org.iana.notifications.template;

import org.iana.notifications.PContent;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Template {

    PContent instantiate(Object data) throws TemplateInstantiationException ;

}
