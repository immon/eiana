package org.iana.rzm.user;

import javax.persistence.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * <p>
 * This class represents an MD5 encoded password.
 * </p>
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class MD5Password extends AbstractPassword {

    /**
     * An MD5 encoded password.
     */
    @Basic
    private String password;

    private MD5Password() {}

    /**
     * Constructs a new MD5 encoded password.
     *
     * @param password the new plain-text password to be set.
     */
    public MD5Password(String password) {
        setPassword(password);
    }

    /**
     * Sets a new password string which becomes an MD5 encoded. A null password is the same as an empty one.
     *
     * @param password the new plain-text password to be set
     *
     * @throws UnsupportedOperationException thrown when MD5 algorithm can not be found.
     */
    public void setPassword(String password) {
        if (password == null) password = "";
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(password.getBytes());
            byte[] md5 = md.digest();
            StringBuffer encoded = new StringBuffer();
            for (byte m : md5) {
                encoded.append(Integer.toHexString(m & 0xff));
            }
            this.password = encoded.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    /**
     * Checks whether a given plain-text password matches this MD5 encoded password.
     *
     * @param password the plain-text password
     * @return true if the plain-text password matches this MD5 encoded password, false otherwise
     */
    public boolean isValid(String password) {
        MD5Password pswd = new MD5Password(password);
        return equals(pswd);
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MD5Password that = (MD5Password) o;

        if (password != null ? !password.equals(that.password) : that.password != null) return false;

        return true;
    }

    public int hashCode() {
        return (password != null ? password.hashCode() : 0);
    }
}
