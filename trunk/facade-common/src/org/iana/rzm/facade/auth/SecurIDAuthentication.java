package org.iana.rzm.facade.auth;

/**
 * @author Patrycja Wegrzynowicz
 */
public class SecurIDAuthentication implements AuthenticationData {

    private String userName;
    private String data;

    public SecurIDAuthentication() {
    }

    public SecurIDAuthentication(String userName, String data) {
        this.userName = userName;
        this.data = data;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void accept(AuthenticationVisitor visitor) throws AuthenticationException {
        visitor.visitSecurID(this);
    }
}
