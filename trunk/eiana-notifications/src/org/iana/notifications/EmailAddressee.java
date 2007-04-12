package org.iana.notifications;

/**
 * @author Piotr Tkaczyk
 */

public class EmailAddressee extends Addressee {
    private String email;
    private String Name;

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
