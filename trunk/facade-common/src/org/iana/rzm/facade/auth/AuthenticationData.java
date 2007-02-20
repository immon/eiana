package org.iana.rzm.facade.auth;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AuthenticationData {  

    void accept(AuthenticationVisitor visitor) throws AuthenticationException;
}
