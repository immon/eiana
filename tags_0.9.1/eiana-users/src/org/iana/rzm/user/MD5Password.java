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
public class MD5Password extends AbstractPassword implements Cloneable {

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
            this.password = hexadecimalConversionMD5(md5);
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    public String getPassword() {
        return password;
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

    public Object clone() throws CloneNotSupportedException {
        MD5Password md5Password = (MD5Password) super.clone();
        md5Password.setObjId(getObjId());
        md5Password.setPassword(password);
        return md5Password;
    }

    private String hexadecimalConversionMD5(byte[] binaryData) {
        final char[] hexadecimal = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

        if (binaryData.length != 16) {
            return null;
        }

        char[] buffer = new char[32];

        for (int i = 0; i < 16; i++) {
            int low = (binaryData[i] & 0x0f);
            int high = ((binaryData[i] & 0xf0) >> 4);
            buffer[i * 2] = hexadecimal[high];
            buffer[i * 2 + 1] = hexadecimal[low];
        }
        return new String(buffer);
    }
    
}
