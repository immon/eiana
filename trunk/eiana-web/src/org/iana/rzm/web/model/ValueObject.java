package org.iana.rzm.web.model;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

import java.io.Serializable;


public class ValueObject implements Serializable {

    //nask_todo make sure nask mark all VO's as Serializable
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.DEFAULT_STYLE);
    }


    public boolean equals(Object object) {
        return EqualsBuilder.reflectionEquals(this, object);
    }

    public int hashCode() {
        return HashCodeBuilder.reflectionHashCode(this);
    }
}
