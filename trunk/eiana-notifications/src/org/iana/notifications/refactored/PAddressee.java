package org.iana.notifications.refactored;

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


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PAddressee that = (PAddressee) o;

        if (email != null ? !email.equals(that.email) : that.email != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }
}
