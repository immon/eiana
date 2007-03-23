/**
 * @author Piotr Tkaczyk
 */
package org.iana.notifications;

import javax.persistence.*;

@Entity
public class EmailAddress implements Addressee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;

    @Basic
    private String eMail;
    @Basic
    private String name;

    private void setObjId(Long objId) {
        this.objId = objId;
    }
    
    private Long getObjId() {
        return this.objId;
    }

    public EmailAddress(String name, String eMail) {
        this.name  = name;
        this.eMail = eMail;
    }

    public String getName() {
        return this.name;
    }

    public String getEmail() {
        return this.eMail;
    }
}
