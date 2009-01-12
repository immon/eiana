package org.iana.rzm.facade.auth.securid;

import org.iana.rzm.facade.auth.*;
import org.iana.secureid.RSAPinData;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface SecurIDService {

    AuthenticatedUser authenticate(String userName, String securId) throws SecurIDNextCodeRequiredException, SecurIDNewPinRequiredException, SecurIDException, AuthenticationFailedException;

    AuthenticatedUser authenticateWithNextCode(String sessionId, String securId) throws SecurIDException;

    void setPin(String sessionId, String pin) throws SecurIDInvalidPinException, SecurIDException;

    RSAPinData getPinInfo() throws SecurIDException;

}
