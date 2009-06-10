package org.iana.notifications.template.simple;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface StringTemplateAlgorithm {

    String instantiateString(String template, Object data) throws StringTemplateException;
    
}
