package org.iana.rzm.facade.auth;

/**
 * @author Jakub Laszkiewicz
 */
public class MailAuth implements AuthenticationData {
    private String email;
    private String domainName;

    public MailAuth() {
    }

    public MailAuth(String email) {
        this.email = email;
    }

    public MailAuth(String email, String domainName) {
        this.email = email;
        this.domainName = domainName;
    }

    public String getUserName() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDomainName() {
        return domainName;
    }

    public void accept(AuthenticationVisitor visitor) throws AuthenticationException {
    }
}
