package org.iana.rzm.facade.auth;

import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserException;
import org.iana.rzm.user.UserManagerFactory;
import org.iana.rzm.facade.user.converter.UserConverter;
import org.iana.rzm.facade.user.converter.ConverterException;
import org.iana.rzm.common.validators.CheckTool;

import java.text.MessageFormat;

/**
 * org.iana.rzm.facade.auth.PasswordAuthenticator
 *
 * @author Marcin Zajaczkowski
 *
 * todo Probably could have common superclass with SecurIDAuth
 */
public class PasswordAuthenticator implements Authenticator {

    public AuthenticatedUser authenticate(AuthenticationData data, UserManager manager) throws AuthenticationException {

        CheckTool.checkNull(data, "AuthenticationData");
        CheckTool.checkNull(manager, "UserManager");

        try {
            RZMUser user = manager.get(data.getUserName());

            if (user == null) {
                throw new AuthenticationFailedException(
                        MessageFormat.format("User {0} has not been found.", data.getUserName()));
            }

            AuthenticatedUser authenticatedUser = new AuthenticatedUser(UserConverter.convert(user));

//        todo authenticatedUser.checkPermission(); or something

            return authenticatedUser;

        } catch (UserException e) {
            throw new AuthenticationException(e);

        } catch (ConverterException e) {
            throw new AuthenticationException(e);
        }
    }
}
