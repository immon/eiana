package org.iana.rzm.facade.auth;

/**
 * <p><code>AuthenticationData</code> interface represents a hierarchy of various credential types.</p>
 *
 * @author Patrycja Wegrzynowicz
 */
public interface AuthenticationData {

    /**
     * Accepts a given visitor (Visitor GoF pattern). Concrete implementations call appropriate methods
     * of <code>AuthenticationVisitor</code> interface (one method per one concrete class).
     *
     * @param visitor the visitor implementation to be accepted by this instance.
     * @throws AuthenticationException when error occured during processing inside the visitor.
     */
    void accept(AuthenticationVisitor visitor) throws AuthenticationException;
}
