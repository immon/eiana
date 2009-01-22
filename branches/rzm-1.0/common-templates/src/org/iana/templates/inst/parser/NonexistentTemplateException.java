package org.iana.templates.inst.parser;

/**
 * @author Jakub Laszkiewicz
 */
public class NonexistentTemplateException extends TemplateParseException {
    public NonexistentTemplateException(String templateName) {
        super("Template: [" + templateName + "] does not exist");
    }
}
