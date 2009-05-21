package org.iana.rzm.facade.admin.config.binded;

import org.iana.rzm.facade.admin.config.impl.ConfigParameterNames;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class VerisignOrgConfig extends BindedParameter {

    public static final String PUBLIC_KEY = ConfigParameterNames.VERISIGN_PUBLIC_KEY;

    public static final String EMAIL = ConfigParameterNames.VERISIGN_EMAIL;

    public VerisignOrgConfig() {
        super(getParameterNames());
    }

    public VerisignOrgConfig(Map<String, String> values) {
        super(values);
    }

    public String getPublicKey() {
        return getValue(PUBLIC_KEY);
    }

    public void setPublicKey(String value) {
        setValue(PUBLIC_KEY, value);
    }

    public String getEmail() {
        return getValue(EMAIL);
    }

    public void setEmail(String value) {
        setValue(EMAIL, value);
    }

    public static List<String> getParameterNames() {
        return Arrays.asList(PUBLIC_KEY, EMAIL);
    }
}
