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

    private AuthenticationService authenticationService;

    public AuthenticationServiceBean(AuthenticationService authenticationService) {
        CheckTool.checkNull(authenticationService, "authentication service");
        this.authenticationService = authenticationService;
    }

    public AuthenticatedUser authenticate(AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        return authenticationService.authenticate(data);
    }

    public AuthenticatedUser authenticate(AuthenticationToken token, AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        return authenticationService.authenticate(token, data);
    }
}
