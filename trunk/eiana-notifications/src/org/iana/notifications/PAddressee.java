package org.iana.notifications;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.io.Serializable;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr Tkaczyk
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

    @Basic
    private boolean emailAsCC = false;

    protected PAddressee() {
    }

    public PAddressee(String name, String email) {
        init(name, email, false);
    }

    public PAddressee(String name, String email, boolean emailAsCC) {
        init(name, email, emailAsCC);
    }

    private void init(String name, String email, boolean emailAsCC) {
        // CheckTool.checkNull(name, "name");
        // CheckTool.checkNull(email, "email");
        this.name = name;
        this.email = email;
        this.emailAsCC = emailAsCC;
    }

    public boolean isCCEmailAddressee() {
        return emailAsCC;
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

    public String toEmailAddressForm() {
        StringBuilder sb = new StringBuilder();
        sb.append("\"");
        sb.append(getName());
        sb.append("\"");
        sb.append("<");
        sb.append(getEmail());
        sb.append(">");
        sb.append(",");

        return sb.toString();
    }

}
