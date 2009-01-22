package org.iana.templates.inst.parser;

/**
 * @author Jakub Laszkiewicz
 */
public class RequiredElementMissingException extends TemplateParseException {
    public RequiredElementMissingException(String elementName) {
        super("Required element missing: [" + elementName + "]");
    }
}
