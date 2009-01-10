package org.iana.securid;

import org.iana.rzm.facade.auth.*;
import org.iana.rzm.facade.auth.securid.*;

public class StatelessSecureIdServiceBean implements SecurIDService {
    private SecurIDService service;

    public StatelessSecureIdServiceBean(SecurIDService service){

        this.service = service;
    }

    public void authenticate(String userName, String securId) throws
                                                              SecurIDNextCodeRequiredException,
                                                              SecurIDNewPinRequiredException,
                                                              SecurIDException,
                                                              AuthenticationFailedException {
        service.authenticate(userName, securId);
    }

    public void authenticateWithNextCode(String sessionId, String securId) throws SecurIDException {
        service.authenticateWithNextCode(sessionId, securId);
    }

    public void setPin(String sessionId, String pin) throws SecurIDInvalidPinException, SecurIDException {
        service.setPin(sessionId, pin);
    }
}
