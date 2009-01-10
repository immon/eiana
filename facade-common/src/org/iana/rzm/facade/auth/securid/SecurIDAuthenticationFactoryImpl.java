package org.iana.rzm.facade.auth.securid;

import org.iana.secureid.*;
import org.iana.secureid.internal.*;

public class SecurIDAuthenticationFactoryImpl implements SecurIDAuthenticationFactory {
    public SecureIdAuthentication createSecurIdAuthentication() {
        return new SecureIdAuthenticationImpl();
    }
}
