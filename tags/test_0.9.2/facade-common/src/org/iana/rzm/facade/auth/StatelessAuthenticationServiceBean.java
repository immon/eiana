package org.iana.rzm.facade.auth;

import org.iana.rzm.common.validators.CheckTool;

import java.util.Map;

/**
 * @author Piotr Tkaczyk
 */
public class StatelessAuthenticationServiceBean implements AuthenticationService {

    private Map<String, AuthenticationService> authenticators;

    public StatelessAuthenticationServiceBean(Map<String, AuthenticationService> authenticators) {
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
