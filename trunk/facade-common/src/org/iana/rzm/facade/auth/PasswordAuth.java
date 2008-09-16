package org.iana.rzm.facade.auth;

import java.io.Serializable;

/**
 * <p>This class holds a user name and plain-text password.</p>
 *
 * @author Patrycja Wegrzynowicz
 */
public class PasswordAuth implements AuthenticationData, Serializable {

    private String userName;
    private String password;

    public PasswordAuth() {
    }

    public PasswordAuth(String userName, String password) {
        this.userName = userName;
        this.password = password;
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

    public void accept(AuthenticationVisitor visitor) throws AuthenticationException {
        visitor.visitPassword(this);
    }
}
