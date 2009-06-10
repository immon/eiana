package org.iana.templates.def.parser;

/**
 * @author Jakub Laszkiewicz
 */
public class TemplatesManagerException extends Exception {
    public TemplatesManagerException() {
        super();
    }

    public TemplatesManagerException(String message) {
        super(message);
    }

    public TemplatesManagerException(String message, Throwable cause) {
        super(message, cause);
    }

    public TemplatesManagerException(Throwable cause) {
        super(cause);
    }
}
