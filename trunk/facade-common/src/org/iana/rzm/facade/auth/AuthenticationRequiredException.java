package org.iana.rzm.facade.auth;

/**
 * <p>This exception is thrown when an authentication is required, like, when attempting to perform a vulnerable
 * operation or in a case of expired session.</p>
 *
 * @author Patrycja Wegrzynowicz
 */
public class AuthenticationRequiredException extends AuthenticationException {

    private AuthenticationToken token;
    private Authentication required;

    public AuthenticationRequiredException(AuthenticationToken token, Authentication required) {
        this.token = token;
        this.required = required;
    }

    public Authentication getRequired() {
        return required;
    }

    public AuthenticationToken getToken() {
        return token;
    }
}
