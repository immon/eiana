package org.iana.rzm.user;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class MD5Password implements Password {

    private Long objId; 
    String password;

    private MD5Password() {}

    public MD5Password(String password) {
        setPassword(password);
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

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
            password = encoded.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException(e);
        }
    }

    private String getPasswordStr() {
        return password;
    }

    private void setPasswordStr(String password) {
        this.password = password;
    }

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
