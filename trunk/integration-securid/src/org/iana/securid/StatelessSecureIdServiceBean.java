package org.iana.securid;

import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.auth.securid.*;
import org.iana.secureid.RSAPinData;

public class StatelessSecureIdServiceBean implements SecurIDService {
    private SecurIDService service;

    public StatelessSecureIdServiceBean(SecurIDService service){

        this.service = service;
    }

    public AuthenticatedUser authenticate(String userName, String securId) throws SecurIDNextCodeRequiredException, SecurIDNewPinRequiredException, SecurIDException, AuthenticationFailedException {
        return service.authenticate(userName, securId);
    }

    public AuthenticatedUser authenticateWithNextCode(String sessionId, String securId) throws SecurIDException {
        return service.authenticateWithNextCode(sessionId, securId);
    }

    public AuthenticatedUser authenticateWithNextCode(AuthenticationToken token, String sessionId, String securId) throws AuthenticationRequiredException, SecurIDException {
        return service.authenticateWithNextCode(token, sessionId, securId);
    }

    public void setPin(String sessionId, String pin) throws SecurIDInvalidPinException, SecurIDException {
        service.setPin(sessionId, pin);
    }

    public RSAPinData getPinInfo() throws SecurIDException {
        return service.getPinInfo();
    }
}
