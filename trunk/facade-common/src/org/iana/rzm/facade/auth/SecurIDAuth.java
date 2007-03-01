package org.iana.rzm.facade.auth;

/**
 * <p>This class holds a user name and SecurID password.</p>
 *
 * @author Patrycja Wegrzynowicz
 */
public class SecurIDAuth implements AuthenticationData {

    private String userName;
    private String password;

    public SecurIDAuth() {
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
        visitor.visitSecurID(this);
    }
}
