package org.iana.dao.statconfig;

/**
 * This exception is thrown when initialization of the statement config failed for some reason. 
 *
 * @author Patrycja Wegrzynowicz
 */
public class ConfigInitializationFailedException extends RuntimeException {

    private String configName;

    public ConfigInitializationFailedException(Throwable cause, String configName) {
        super(cause);
        this.configName = configName;
    }

    public String getConfigName() {
        return configName;
    }
}
