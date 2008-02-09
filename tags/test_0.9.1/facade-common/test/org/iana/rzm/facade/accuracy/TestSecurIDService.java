package org.iana.rzm.facade.accuracy;

import org.iana.securid.SecurIDService;
import org.iana.securid.InvalidAuthenticationDataException;

/**
 * @author Patrycja Wegrzynowicz
 * @author Marcin Zajaczkowski
 */
public class TestSecurIDService implements SecurIDService {

    public static String ADMIN_WITH_SECURID_SECURID_VALID_LOGIN = TestUserManager.ADMIN_WITH_SECURID_VALID_LOGIN;
    public static String ADMIN_WITH_SECURID_SECURID_VALID_PASSWORD = "adminWithSecurIDSecurdIDPassword";
    public static String ADMIN_WITH_SECURID_SECURID_WRONG_PASSWORD = "bad" + ADMIN_WITH_SECURID_SECURID_VALID_PASSWORD;

    public void authenticate(String userName, String securID) throws InvalidAuthenticationDataException {

        if (ADMIN_WITH_SECURID_SECURID_VALID_LOGIN.equals(userName) &&
            ADMIN_WITH_SECURID_SECURID_VALID_PASSWORD.equals(securID)) {
            return;
        }

        throw new InvalidAuthenticationDataException();
    }
}
