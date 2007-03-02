package org.iana.rzm.trans;

import javax.persistence.*;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public class StateTransition {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Basic
    private String name;

    public StateTransition() {
    }

    public StateTransition(String name) {
        this.name = name;
    }

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
