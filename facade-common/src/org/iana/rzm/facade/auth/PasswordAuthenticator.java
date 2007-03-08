package org.iana.rzm.facade.auth;

import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserException;
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

        //maybe could be done better?
        if (!(data instanceof PasswordAuth)) {
            throw new IllegalArgumentException("Wrong type of AuthenticationData: " + data.getClass().getName());
        }

        PasswordAuth passData = (PasswordAuth)data;

        try {
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

        } catch (UserException e) {
            throw new AuthenticationException(e);

        } catch (ConverterException e) {
            throw new AuthenticationException(e);
        }
    }
}
