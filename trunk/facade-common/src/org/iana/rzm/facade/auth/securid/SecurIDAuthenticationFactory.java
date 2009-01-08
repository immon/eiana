package org.iana.rzm.facade.auth.securid;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface SecurIDAuthenticationFactory {

    org.iana.securid.SecurIdAuthentication createSecurIdAuthentication();
    
}
