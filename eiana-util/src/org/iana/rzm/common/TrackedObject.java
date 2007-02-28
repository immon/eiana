package org.iana.rzm.common;

import javax.persistence.*;
import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
@Entity
public abstract class TrackedObject {

    private Long objId;
    private Long id;
    private Timestamp created;
    private String createdBy;
    private Timestamp modified;
    private String modifiedBy;

    public TrackedObject() {
        createNow();
    }

    public TrackedObject(long id) {
        this();
        this.id = id;
    }

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    @Transient
    final public Long getId() {
        return id;
    }

    final public void setId(Long id) {
        this.id = id;
    }

    @Column(name = "id")
    protected Long getTOId() {
        return id;
    }

    protected void setTOId(Long id) {
        this.id = id;
    }

    @Transient
    final public Timestamp getCreated() {
        return created;
    }

    final public void setCreated(Timestamp created) {
        this.created = created;
    }

    @Column(name = "created")
    protected Timestamp getTOCreated() {
        return created;
    }

    protected void setTOCreated(Timestamp created) {
        this.created = created;
    }

    final public void createNow() {
        setCreated(currentTimestamp());
    }

    @Transient
    final public Timestamp getModified() {
        return modified;
    }

    final public void setModified(Timestamp modified) {
        this.modified = modified;
    }

    @Column(name = "modified")
    protected Timestamp getTOModified() {
        return modified;
    }

    protected void setTOModified(Timestamp modified) {
        this.modified = modified;
    }

    private Timestamp currentTimestamp() {
        return new Timestamp(System.currentTimeMillis());
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }
}
