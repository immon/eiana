package org.iana.rzm.facade.auth;

import org.iana.rzm.user.UserManager;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.UserException;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.facade.user.converter.ConverterException;
import org.iana.rzm.facade.user.converter.UserConverter;

import java.text.MessageFormat;

/**
 * org.iana.rzm.facade.auth.SecurIDAuthenticator
 *
 * @author Marcin Zajaczkowski
 */
public class SecurIDAuthenticator implements Authenticator {

    //todo token should be also passed
    public AuthenticatedUser authenticate(AuthenticationData data, UserManager manager) throws AuthenticationException {

        CheckTool.checkNull(data, "AuthenticationData");
        CheckTool.checkNull(manager, "UserManager");

        //get from our config, has to be valid,
        SecurIDAuth securData = (SecurIDAuth)data;

        try {
            RZMUser user = manager.get(securData.getUserName());

            if (user == null) {
                throw new AuthenticationFailedException(
                        MessageFormat.format("User {0} has not been found.", data.getUserName()));
            }

            if (!user.isValidPassword(securData.getPassword())) {
                throw new AuthenticationFailedException("Password is not valid.");
            }

            //todo Add token verification and securID password

            return new AuthenticatedUser(UserConverter.convert(user));

        } catch (UserException e) {
            throw new AuthenticationException(e);

        } catch (ConverterException e) {
            throw new AuthenticationException(e);
        }
    }
}
