package org.iana.config;


import javax.persistence.*;

/**
 * @author Piotr Tkaczyk
 */

@Entity
@Table(name = "Parameters")
public class AbstractParameter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;

    /**
     * The name of the parameter. Cannot be null.
     */
    @Basic
    protected String name;

    @Basic
    protected String owner;

    /**
     * Parameter validity date.
     */
    @Basic
    protected Long fromDate;
    @Basic
    protected Long toDate;


    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().length() == 0)
            throw new IllegalArgumentException("null or empty parameter name");
        this.name = name;
    }

    public Long getFromDate() {
        return fromDate;
    }

    public void setFromDate(Long fromDate) {
        this.fromDate = fromDate;
    }

    public Long getToDate() {
        return toDate;
    }

    public void setToDate(Long toDate) {
        this.toDate = toDate;
    }
}
