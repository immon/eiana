package org.iana.rzm.facade.auth;

/**
 * @author Piotr Tkaczyk
 */
public class USDoCAuthenticatedUser extends AuthenticatedUser {

    private static final String USDOC_SPECIAL_USER = "usdoc_special_user";

    public USDoCAuthenticatedUser() {
        super(-1, USDOC_SPECIAL_USER, true);
    }
}
