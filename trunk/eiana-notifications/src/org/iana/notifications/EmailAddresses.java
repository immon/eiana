/**
 * @author Piotr Tkaczyk
 */
package org.iana.notifications;

import javax.persistence.*;
import java.util.Collection;

@Entity
public class EmailAddresses {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "EmailAddresses_Addresses",
            inverseJoinColumns = @JoinColumn(name = "EmailAddress_objId"))
    private Collection<EmailAddress> addresses;

    private void setObjId(Long objId) {
        this.objId = objId;
    }

    private Long getObjId() {
        return this.objId;
    }

    public EmailAddresses(Collection<EmailAddress> addresses) {
        this.addresses = addresses;
    }

    public void setAddresses(Collection<EmailAddress> addresses) {
        this.addresses = addresses;
    }

    public Collection<EmailAddress> getAddressees() {
        return this.addresses;
    }
}
