package org.iana.codevalues;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.LazyCollection;

import javax.persistence.Entity;
import javax.persistence.Basic;
import javax.persistence.JoinTable;
import javax.persistence.Column;
import java.util.List;
import java.util.ArrayList;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
class Code {
    @Basic
    private String code;
    @CollectionOfElements
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

