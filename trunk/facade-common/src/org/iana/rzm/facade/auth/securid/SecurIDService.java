package org.iana.rzm.facade.auth.securid;

import org.iana.rzm.facade.auth.*;
import org.iana.secureid.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface SecurIDService {

    AuthenticatedUser authenticate(String userName, String securId) throws SecurIDNextCodeRequiredException, SecurIDNewPinRequiredException, SecurIDException, AuthenticationFailedException;

    AuthenticatedUser authenticateWithNextCode(AuthenticationToken token, String sessionId, String securId) throws AuthenticationRequiredException, SecurIDException;

    void setPin(String sessionId, String pin) throws SecurIDInvalidPinException, SecurIDException;

    public RSAPinData getPinInfo(String sessionId) throws SecurIDException;

}
