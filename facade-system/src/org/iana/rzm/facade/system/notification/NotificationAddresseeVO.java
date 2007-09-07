package org.iana.rzm.facade.system.notification;

/**
 * @author Jakub Laszkiewicz
 */
public class NotificationAddresseeVO {
    private String name;
    private String email;

    public NotificationAddresseeVO(String name, String email) {
        this.name = name;
        this.email = email;
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