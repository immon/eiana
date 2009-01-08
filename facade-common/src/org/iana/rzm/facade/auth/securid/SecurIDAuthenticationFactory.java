package org.iana.rzm.facade.auth.securid;

import org.iana.secureid.*;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface SecurIDAuthenticationFactory {

    public SecureIdAuthentication createSecurIdAuthentication();
    
}
