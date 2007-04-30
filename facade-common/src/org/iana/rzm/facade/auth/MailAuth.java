package org.iana.rzm.facade.auth;

/**
 * @author Jakub Laszkiewicz
 */
public class MailAuth implements AuthenticationData {
    private String email;

    public MailAuth() {
    }

    public MailAuth(String email) {
        this.email = email;
    }

    public String getUserName() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void accept(AuthenticationVisitor visitor) throws AuthenticationException {
    }
}
