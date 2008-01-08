package org.iana.templates.validator;

/**
 * @author Jakub Laszkiewicz
 */
public class RegexFailedException extends ValidationException {
    public RegexFailedException(String name, String value, String regex) {
        super("Regex validation failed on element [" + name + "] value [" + value + "] with regex [" + regex + "]");
    }
}
