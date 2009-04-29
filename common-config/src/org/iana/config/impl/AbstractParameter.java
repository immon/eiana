package org.iana.config.impl;


import org.iana.config.Parameter;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

/**
 * @author Piotr Tkaczyk
 */

@Entity
@Table(name = "Parameters")
abstract class AbstractParameter implements Parameter {

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

    protected AbstractParameter() {
        this.fromDate = System.currentTimeMillis();
    }

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

    public abstract String getParameter();

    public abstract List<String> getParameterList();

    public abstract Set<String> getParameterSet();
}
