package org.iana.rzm.trans.confirmation.contact;

import org.iana.notifications.AbstractAddressee;
import org.iana.notifications.Addressee;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.user.Role;
import org.iana.rzm.auth.Identity;

import javax.persistence.*;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class ContactIdentity extends AbstractAddressee implements Identity, Cloneable {

    @Enumerated
    private SystemRole.SystemType type;
    @Basic
    private String name;
    @Basic
    private String email;
    @Basic
    private String token;
    @Basic
    private boolean newContact;

    private ContactIdentity() {
    }

    public ContactIdentity(String token) {
        this.token = token;
    }

    public ContactIdentity(SystemRole.SystemType type, Contact contact, String token, boolean newContact) {
        this(type, contact.getName(), contact.getEmail(), token, newContact);
    }

    public ContactIdentity(SystemRole.SystemType type, String name, String email, String token, boolean newContact) {
        this.type = type;
        this.name = name;
        this.email = email == null ? "" : email;
        this.token = token;
        this.newContact = newContact;
    }

    public SystemRole.Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getToken() {
        return token;
    }

    public boolean isNewContact() {
        return newContact;
    }

    public void setNewContact(boolean newContact) {
        this.newContact = newContact;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ContactIdentity that = (ContactIdentity) o;

        if (token != null ? !token.equals(that.token) : that.token != null) return false;

        return true;
    }

    public int hashCode() {
        return (token != null ? token.hashCode() : 0);
    }

    public ContactIdentity clone() {
        ContactIdentity ret = (ContactIdentity) super.clone();
        ret.setObjId(null);
        return ret;
    }
}
