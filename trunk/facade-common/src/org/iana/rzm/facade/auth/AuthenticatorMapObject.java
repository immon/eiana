package org.iana.rzm.facade.auth;

import org.iana.rzm.facade.auth.Authenticator;

import java.util.Map;
import java.util.Collections;

/**
 * org.iana.rzm.facade.auth.factory.AuthenticatorMap
 *
 * @author Marcin Zajaczkowski
 */
public class AuthenticatorMapObject {

    private Map<String, Authenticator> authenticatorsMap;


    public Map<String, Authenticator> getAuthenticatorsMap() {
        return Collections.unmodifiableMap(authenticatorsMap);
    }

    public void setAuthenticatorsMap(Map<String, Authenticator> authenticatorsMap) {
        this.authenticatorsMap = authenticatorsMap;
    }

    
}
