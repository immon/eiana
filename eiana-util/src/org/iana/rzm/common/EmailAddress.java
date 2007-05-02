package org.iana.rzm.common;

import org.iana.rzm.common.exceptions.InvalidEmailException;
import org.iana.rzm.common.exceptions.InvalidNameException;

import javax.persistence.Basic;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Locale;

/**
 * @author Jakub Laszkiewicz
 */
@Embeddable
public class EmailAddress implements Cloneable, Serializable {
    @Basic
    private String email;

    private EmailAddress() {
    }

    public EmailAddress(String email) {
        if (email == null) throw new NullPointerException("email is null");
        email = email.toLowerCase(Locale.ENGLISH);
        isValidEmail(email);
        this.email = email;
    }

    public String getEmail() {
        return email;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        EmailAddress that = (EmailAddress) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;

        return true;
    }

    public int hashCode() {
        return (email != null ? email.hashCode() : 0);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    private static String EMAIL_PATTERN = "[a-z0-9]+([_\\.\\-][a-z0-9]+)*@([a-z0-9]+([\\.\\-][a-z0-9]+)*)+\\.[a-z]{2,}";

    private void isValidEmail(String email) throws InvalidNameException {
        if (email != null && !email.matches(EMAIL_PATTERN)) throw new InvalidEmailException(email);
    }
}
