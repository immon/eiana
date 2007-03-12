package org.iana.rzm.facade.auth;

import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.securid.SecurIDService;
import org.iana.securid.InvalidAuthenticationDataException;

import java.text.MessageFormat;

/**
 * org.iana.rzm.facade.auth.SecurIDAuthenticator
 *
 * @author Marcin Zajaczkowski
 * @author Patrycja Wegrzynowicz
 */
public class SecurIDAuthenticator implements AuthenticationService {

    private UserManager manager;
    private SecurIDService securID;

    public SecurIDAuthenticator(UserManager manager, SecurIDService securID) {
        CheckTool.checkNull(manager, "user manager");
        CheckTool.checkNull(securID, "securID service");
        this.manager = manager;
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
        RZMUser user = manager.get(securData.getUserName());
        if (user == null) {
            throw new AuthenticationFailedException(
                    MessageFormat.format("User {0} has not been found.", data.getUserName()));
        }
        try {
            securID.authenticate(securData.getUserName(), securData.getPassword());
            return new AuthenticatedUser(UserConverter.convert(user));
        } catch (InvalidAuthenticationDataException e) {
            throw new AuthenticationFailedException(e);
        }
    }
}
