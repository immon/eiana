package org.iana.codevalues;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"code"}))
public class Code implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;
    @Basic
    private String code;
    @OneToMany(cascade = CascadeType.ALL)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinTable(name = "code_values")
    @LazyCollection(LazyCollectionOption.FALSE)
    private List<Value> values = new ArrayList<Value>();

    private Code() {
    }

    public Code(String code) {
        this(code, new ArrayList<Value>());
    }

    public Code(String code, List<Value> values) {
        this(null, code, values);
    }

    public Code(Long objId, String code, List<Value> values) {
        if (code == null) throw new IllegalArgumentException("code cannot be null");
        if (values == null) throw new IllegalArgumentException("values cannot be null");
        this.objId = objId;
        this.code = code;
        this.values = values;
    }

    public Long getObjId() {
        return objId;
    }

    public String getCode() {
        return code;
    }

    public List<Value> getValues() {
        return values;
    }

    public void addValue(Value value) {
        if (value == null) throw new IllegalArgumentException("value cannot be null");
        values.add(value);
    }
}

