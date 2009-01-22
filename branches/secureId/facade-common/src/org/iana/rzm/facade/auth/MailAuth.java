package org.iana.rzm.facade.auth;

/**
 * @author Jakub Laszkiewicz
 */
public class MailAuth implements AuthenticationData {

    private String email;

    private String domainName;

    private String pgpSignature;

    public MailAuth() {
    }

    public MailAuth(String email) {
        this.email = email;
    }

    public MailAuth(String email, String domainName) {
        this.email = email;
        this.domainName = domainName;
    }


    public MailAuth(String email, String domainName, String pgpSignature) {
        this.email = email;
        this.domainName = domainName;
        this.pgpSignature = pgpSignature;
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

    public String getPgpSignature() {
        return pgpSignature;
    }

    public void accept(AuthenticationVisitor visitor) throws AuthenticationException {
    }
}
