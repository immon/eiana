package org.iana.rzm.user;

import java.sql.Timestamp;

/**
 * <p>
 * The password interface to generlize manipulation of different forms of passwords (MD5, plain-text, SHA-1 etc.).
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 */
public interface Password extends Cloneable {

    /**
     * Sets a new password.
     *
     * @param password the plain-text new password
     */
    public void setPassword(String password);

    /**
     * Returns a password string in a format specific to this object.
     *
     * @return the password string
     */
    public String getPassword();

    /**
     * Checks whether a given plain-text password matches this one.
     *
     * @param password the plain-text password
     * @return true if the given plain-text password matches this one, false otherwise
     */
    public boolean isValid(String password);

    public boolean isExpired();

    public Object clone() throws CloneNotSupportedException;

    public Timestamp getExDate();

    public void setExDate(Timestamp exDate);
}
