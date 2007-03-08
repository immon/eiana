package org.iana.rzm.facade.auth;

import org.apache.log4j.Logger;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.user.UserManager;

import javax.ejb.Stateless;
import java.util.Map;
import java.util.Collections;
import java.text.MessageFormat;

/**
 * @author Marcin Zajaczkowski
 * @author Patrycja Wegrzynowicz
 */

@Stateless
public class AuthenticationServiceBean implements AuthenticationService {

    private final Logger loger = Logger.getLogger(getClass());

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
