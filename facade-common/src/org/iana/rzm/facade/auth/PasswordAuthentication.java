package org.iana.rzm.facade.auth;

/**
 * @author Patrycja Wegrzynowicz
 */
public class PasswordAuthentication implements AuthenticationData {

    private String userName;
    private String password;

    public PasswordAuthentication() {
    }

    public PasswordAuthentication(String userName, String password) {
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
