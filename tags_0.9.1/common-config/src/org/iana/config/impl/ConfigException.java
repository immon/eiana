package org.iana.config.impl;

/**
 * @author Piotr Tkaczyk
 */
public class ConfigException extends Exception {

    public ConfigException() {
    }

    public ConfigException(String string) {
        super(string);
    }

    public ConfigException(String string, Throwable throwable) {
        super(string, throwable);
    }

    public ConfigException(Throwable throwable) {
        super(throwable);
    }
}