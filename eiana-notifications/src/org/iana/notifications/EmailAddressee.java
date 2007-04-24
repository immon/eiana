package org.iana.notifications;

import javax.persistence.Entity;
import javax.persistence.Basic;

/**
 * @author Piotr Tkaczyk
 */
@Entity
public class EmailAddressee extends Addressee {

    @Basic
    private String email;
    @Basic
    private String name;

    public EmailAddressee(String email, String name) {
        this.email = email;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
