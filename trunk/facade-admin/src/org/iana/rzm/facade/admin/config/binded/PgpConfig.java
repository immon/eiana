package org.iana.rzm.facade.admin.config.binded;

import org.iana.rzm.facade.admin.config.ConfigParameterNames;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class PgpConfig extends BindedParameter {

    public static final String PRIVATE_KEY = ConfigParameterNames.EMAIL_PGP_PRIVATE_KEY;
    public static final String PRIVATE_KEY_FILENAME = ConfigParameterNames.EMAIL_PGP_PRIVATE_KEY_FILENAME;
    public static final String PRIVATE_KEY_PASSPHRASE = ConfigParameterNames.EMAIL_PGP_PRIVATE_KEY_PASSPHRASE;

    public PgpConfig() {
        super(getParameterNames());
    }

    public PgpConfig(Map<String, String> values) {
        super(values);
    }

    public String getPrivateKey() {
        return getValue(PRIVATE_KEY);   
    }

    public void setPrivateKey(String value) {
        setValue(PRIVATE_KEY, value);
    }

    public String getPrivateKeyFilename() {
        return getValue(PRIVATE_KEY_FILENAME);
    }

    public void setPrivateKeyFilename(String value) {
        setValue(PRIVATE_KEY_FILENAME, value);
    }

    public String getPrivateKeyPassPhrase() {
        return getValue(PRIVATE_KEY_PASSPHRASE);
    }

    public void setPrivateKeyPassPhrase(String value) {
        setValue(PRIVATE_KEY_PASSPHRASE, value);
    }

    public static List<String> getParameterNames() {
        return Arrays.asList(PRIVATE_KEY, PRIVATE_KEY_FILENAME, PRIVATE_KEY_PASSPHRASE);
    }
}
