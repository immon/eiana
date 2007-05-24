package org.iana.codevalues;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.Cascade;

import javax.persistence.*;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class Code {

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

    public Code(String code, List<Value> values) {
        this.code = code;
        this.values = values;
    }

    public String getCode() {
        return code;
    }

    public List<Value> getValues() {
        return values;
    }
}

