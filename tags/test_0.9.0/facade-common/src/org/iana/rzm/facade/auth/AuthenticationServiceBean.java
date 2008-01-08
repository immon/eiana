package org.iana.rzm.facade.auth;

import org.apache.log4j.Logger;
import org.iana.rzm.common.validators.CheckTool;

import javax.ejb.Stateless;
import java.util.Map;

/**
 * @author Marcin Zajaczkowski
 * @author Patrycja Wegrzynowicz
 */
public class AuthenticationServiceBean implements AuthenticationService {

    private Map<String, AuthenticationService> authenticators;

    public AuthenticationServiceBean(Map<String, AuthenticationService> authenticators) {
        CheckTool.checkNull(authenticators, "authenticators");
        this.authenticators = authenticators;
    }

    public AuthenticatedUser authenticate(AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        CheckTool.checkNull(data, "authentication data");
        return getAuthenticator(data).authenticate(data);
    }

    public AuthenticatedUser authenticate(AuthenticationToken token, AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        CheckTool.checkNull(data, "authentication data");
        return getAuthenticator(data).authenticate(token, data);
    }

    private AuthenticationService getAuthenticator(AuthenticationData data) {
        AuthenticationService authenticator = authenticators.get(data.getClass().getName());
        CheckTool.checkNull(authenticator, "authenticator for authentication data " + data.getClass());
        return authenticator;
    }
}
