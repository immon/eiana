package org.iana.rzm.web.common.model;

import org.apache.commons.lang.builder.*;

import java.io.*;


public class ValueObject implements Serializable {

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
