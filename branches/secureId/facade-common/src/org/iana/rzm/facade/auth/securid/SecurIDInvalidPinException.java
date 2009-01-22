package org.iana.rzm.facade.auth.securid;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SecurIDInvalidPinException extends SecurIDException {
    private String sessionId;

    public SecurIDInvalidPinException(String message, String sessionId) {
        super(message);
        this.sessionId = sessionId;
    }

    public String getSessionId(){
        return sessionId;
    }
}
