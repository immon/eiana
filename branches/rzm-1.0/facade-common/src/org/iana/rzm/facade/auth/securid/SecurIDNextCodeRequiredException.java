package org.iana.rzm.facade.auth.securid;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SecurIDNextCodeRequiredException extends SecurIDException {

    private String sessionId;

    public SecurIDNextCodeRequiredException(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSessionId() {
        return sessionId;
    }
}
