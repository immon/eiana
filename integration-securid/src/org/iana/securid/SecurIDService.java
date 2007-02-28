package org.iana.securid;

/**
 * <p>
 * This interface is a boundary interface dedicated to establish a common contract between the application
 * and SecurID authentication server.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public interface SecurIDService {

    void authenticate(String userName, String securID) throws InvalidAuthenticationDataException;
}
