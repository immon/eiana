package org.iana.securid;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface SecurIDService {

    void authenticate(String userName, String securID) throws InvalidAuthenticationDataException;
}
