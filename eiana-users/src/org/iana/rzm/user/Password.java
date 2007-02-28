package org.iana.rzm.user;

/**
 * <p>
 * The password interface to generlize manipulation of different forms of passwords (MD5, plain-text, SHA-1 etc.).
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public interface Password {

    /**
     * Sets a new password.
     *
     * @param password the plain-text new password
     */
    public void setPassword(String password);

    /**
     * Checks whether a given plain-text password matches this one.
     *
     * @param password the plain-text password
     * @return true if the given plain-text password matches this one, false otherwise
     */
    public boolean isValid(String password);
}
