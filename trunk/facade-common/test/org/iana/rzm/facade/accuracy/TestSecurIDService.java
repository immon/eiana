package org.iana.rzm.facade.accuracy;

import org.iana.securid.SecurIDService;
import org.iana.securid.InvalidAuthenticationDataException;

/**
 * @author Patrycja Wegrzynowicz
 * @author Marcin Zajaczkowski
 */
public class TestSecurIDService implements SecurIDService {

    public static String ADMIN_WITH_SECURID_SECURID_LOGIN_VALID = TestUserManager.ADMIN_WITH_SECURID_LOGIN_VALID;
    public static String ADMIN_WITH_SECURID_SECURID_PASSWORD_VALID = "adminWithSecurIDSecurdIDPassword";

    public void authenticate(String userName, String securID) throws InvalidAuthenticationDataException {

        if (ADMIN_WITH_SECURID_SECURID_LOGIN_VALID.equals(userName) &&
            ADMIN_WITH_SECURID_SECURID_PASSWORD_VALID.equals(securID)) {
            return;
        }

        throw new InvalidAuthenticationDataException();
    }
}
