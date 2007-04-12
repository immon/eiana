package org.iana.notifications;

import javax.persistence.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Piotr    Tkaczyk
 */

@Entity
@Inheritance(strategy=InheritanceType.JOINED)
public abstract class Addressee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public abstract String getEmail();
    public abstract String getName();

    

    
}
