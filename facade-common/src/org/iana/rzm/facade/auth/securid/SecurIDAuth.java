package org.iana.rzm.facade.auth.securid;

import org.iana.rzm.facade.auth.AuthenticationData;
import org.iana.rzm.facade.auth.AuthenticationVisitor;
import org.iana.rzm.facade.auth.AuthenticationException;

/**
 * <p>This class holds a user name and SecurID password.</p>
 *
 * @author Patrycja Wegrzynowicz
 */
public class SecurIDAuth implements AuthenticationData {

    private String userName;
    private String password;
    private String sessionId;

    public SecurIDAuth() {
    }

    public SecurIDAuth(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public SecurIDAuth(String userName, String password, String sessionID) {
        this.userName = userName;
        this.password = password;
        this.sessionId = sessionID;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public void accept(AuthenticationVisitor visitor) throws AuthenticationException {
        visitor.visitSecurID(this);
    }
}
