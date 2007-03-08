package org.iana.rzm.facade.auth;

import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.common.validators.CheckTool;

import java.text.MessageFormat;

/**
 * org.iana.rzm.facade.auth.PasswordAuthenticator
 *
 * @author Marcin Zajaczkowski
 * @author Patrycja Wegrzynowicz
 */
public class PasswordAuthenticator implements AuthenticationService {

    private UserManager manager;

    public PasswordAuthenticator(UserManager manager) {
        CheckTool.checkNull(manager, "user manager");
        this.manager = manager;
    }

    public AuthenticatedUser authenticate(AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        CheckTool.checkNull(data, "AuthenticationData");
        CheckTool.checkNull(manager, "UserManager");

        PasswordAuth passData = (PasswordAuth)data;
        RZMUser user = manager.get(passData.getUserName());
        if (user == null) {
            throw new AuthenticationFailedException(
                    MessageFormat.format("User {0} has not been found.", data.getUserName()));
        }
        if (!user.isValidPassword(passData.getPassword())) {
            throw new AuthenticationFailedException("Password is not valid.");
        }
        if (user.isSecurID()) {
            AuthenticationToken token = new AuthenticationToken(data.getUserName(), Authentication.PASSWORD);
            throw new AuthenticationRequiredException(token, Authentication.SECURID);
        }
        return new AuthenticatedUser(UserConverter.convert(user));
    }

    public AuthenticatedUser authenticate(AuthenticationToken token, AuthenticationData data) throws AuthenticationFailedException, AuthenticationRequiredException {
        throw new AuthenticationRequiredException(Authentication.SECURID);
    }
}
