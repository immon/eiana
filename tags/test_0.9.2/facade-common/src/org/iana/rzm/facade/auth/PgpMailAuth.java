package org.iana.rzm.facade.auth;

/**
 * @author Jakub Laszkiewicz
 */
public class PgpMailAuth implements AuthenticationData {
    private String email;
    private String message;

    public PgpMailAuth() {
    }

    public PgpMailAuth(String email, String message) {
        this.email = email;
        this.message = message;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUserName() {
        return email;
    }

    public void accept(AuthenticationVisitor visitor) throws AuthenticationException {
    }
}
