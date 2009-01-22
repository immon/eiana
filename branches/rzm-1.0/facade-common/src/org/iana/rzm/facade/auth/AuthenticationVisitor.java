package org.iana.rzm.facade.auth;

import org.iana.rzm.facade.auth.securid.*;

/**
 * <p><code>AuthenticationVisitor</code> represents an operation to be performed on the elements of the <code>AuthenticationData</code>
 * hierarchy. It's a straightforward implementation of Visitor GoF design pattern altough
 * the motivation behind such a decision has been a bit different to GoF's one. The Visitor pattern has been used
 * to provide a single point of access to the authentication services and hide logging-in logic from the client
 * of this package. These constraints led to the design with a single <code>AuthenticationService</code>
 * and a hierarchy of <code>AuthenticationData</code> classes. To facilitate <code>AuthenticationData</code> dispatching
 * <code>AuthenticationVisitor</code> has been introduced. Altough Chain of Responsibility GoF design pattern
 * may look more appropriate in this case Visitor has been chosen as a simple alternative with a static type checking.</p> 
 *
 * @author Patrycja Wegrzynowicz
 */
public interface AuthenticationVisitor  {

    void visitPassword(PasswordAuth data) throws AuthenticationException;

    void visitSecurID(SecurIDAuth data) throws AuthenticationException;

    AuthenticatedUser getAuthenticatedUser() throws AuthenticationException;
}
