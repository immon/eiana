package org.iana.rzm.trans.confirmation.contact;

import org.iana.rzm.domain.Contact;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.trans.confirmation.Identity;

import javax.persistence.*;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class ContactIdentity implements Identity, Cloneable {

    @Id @GeneratedValue
    private Long objId;
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
    @Basic
    private boolean sharedEffect;
    @Basic
    private String domainName;

    protected ContactIdentity() {
    }

    public ContactIdentity(String token) {
        this.token = token;
    }

    public ContactIdentity(SystemRole.SystemType type, Contact contact, String token, boolean newContact, String domainName) {
        this(type, contact.getName(), contact.getEmail(), token, newContact, false, domainName);
    }

    public ContactIdentity(SystemRole.SystemType type, Contact contact, String token, boolean newContact, boolean sharedEffect, String domainName) {
        this(type, contact.getName(), contact.getEmail(), token, newContact, sharedEffect, domainName);
    }

    public ContactIdentity(SystemRole.SystemType type, String name, String email, String token, boolean newContact, boolean sharedEffect, String domainName) {
        this.type = type;
        this.name = name;
        this.email = email == null ? "" : email;
        this.token = token;
        this.newContact = newContact;
        this.sharedEffect = sharedEffect;
        this.domainName = domainName;
    }

    public Long getObjId() {
        return objId;
    }

    public SystemRole.SystemType getType() {
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
        try {
            ContactIdentity ret = (ContactIdentity) super.clone();
            ret.objId = null;
            return ret;
        } catch (CloneNotSupportedException e) {
            throw new UnsupportedOperationException();
        }
    }

    public boolean isSharedEffect() {
        return sharedEffect;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getNameOfIdentity() {
        StringBuilder ret = new StringBuilder();
        ret.append("[").append(type).append(": ").append(name).append("]");
        return ret.toString();
    }

}
