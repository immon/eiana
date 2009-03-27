package org.iana.rzm.facade.auth;

/**
 * @author Piotr Tkaczyk
 */
public class VerisignAuthenticatedUser extends AuthenticatedUser {

    private static final String VERISIGN_SPECIAL_USER = "verisign_special_name";

    public VerisignAuthenticatedUser() {
        super(-1, VERISIGN_SPECIAL_USER, true);
    }
}
