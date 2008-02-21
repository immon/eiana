package org.iana.notifications;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class PAddressee implements Serializable {

    @Id
    @GeneratedValue
    Long id;
    
    @Basic
    private String name;

    @Basic
    private String email;

    protected PAddressee() {
    }

    public PAddressee(String name, String email) {
        init(name, email);
    }

    private void init(String name, String email) {
        // CheckTool.checkNull(name, "name");
        // CheckTool.checkNull(email, "email");
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
