package org.iana.rzm.user;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
@Table(name = "Password")
public abstract class AbstractPassword implements Password {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;

    private Timestamp exDate;

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public boolean isExpired() {
        return exDate != null && exDate.getTime() < System.currentTimeMillis();
    }

    public Timestamp getExDate() {
        return exDate;
    }

    public void setExDate(Timestamp exDate) {
        this.exDate = exDate;
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
}
