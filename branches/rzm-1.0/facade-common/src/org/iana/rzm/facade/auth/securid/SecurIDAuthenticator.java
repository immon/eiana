package org.iana.rzm.facade.auth.securid;

import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.auth.*;

/**
 * org.iana.rzm.facade.auth.SecurIDAuthenticator
 *
 * @author Marcin Zajaczkowski
 * @author Patrycja Wegrzynowicz
 */
public class SecurIDAuthenticator implements AuthenticationService {

    private SecurIDService securID;

    public SecurIDAuthenticator(SecurIDService securID) {
        CheckTool.checkNull(securID, "securID service");
        this.securID = securID;
    }

    public AuthenticatedUser authenticate(AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        CheckTool.checkNull(data, "authentication data");
        throw new AuthenticationRequiredException(Authentication.PASSWORD);
    }

    public AuthenticatedUser authenticate(AuthenticationToken token, AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        CheckTool.checkNull(token, "authentication token");
        CheckTool.checkNull(data, "authentication data");
        if (!token.hasCredential(Authentication.PASSWORD)) throw new AuthenticationRequiredException(Authentication.PASSWORD);

        SecurIDAuth securData = (SecurIDAuth)data;
        return securID.authenticate(securData.getUserName(), securData.getPassword());
    }
    
}
