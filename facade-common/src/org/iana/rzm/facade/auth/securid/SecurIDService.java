package org.iana.rzm.facade.auth.securid;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface SecurIDService {

    void authenticate(String userName, String securId) throws SecurIDNextCodeRequiredException, SecurIDNewPinRequiredException, SecurIDException;

    void authenticateWithNextCode(String sessionId, String securId) throws SecurIDException;

    void setPin(String sessionId, String pin) throws SecurIDInvalidPinException, SecurIDException;
    
}
