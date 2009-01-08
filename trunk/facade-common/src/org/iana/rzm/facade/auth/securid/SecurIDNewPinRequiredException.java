package org.iana.rzm.facade.auth.securid;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SecurIDNewPinRequiredException extends SecurIDException {

    private String sessionId;

    public SecurIDNewPinRequiredException(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
