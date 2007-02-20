package org.iana.rzm.facade.auth;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface AuthenticationVisitor {

    void visitPassword(PasswordAuthentication data) throws AuthenticationException;

    void visitSecurID(SecurIDAuthentication data) throws AuthenticationException;
}
