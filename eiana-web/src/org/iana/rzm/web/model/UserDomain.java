package org.iana.rzm.web.model;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.iana.rzm.web.util.DateUtil;

import java.io.Serializable;
import java.util.Date;

public class UserDomain implements Serializable, Comparable<UserDomain> {

    private long domainId;
    private String domainName ;
    private String roleType ;
    private String modified;


    public UserDomain(long domainId, String domainName, String roleType, Date modified) {
        this.domainId = domainId;
        this.domainName = domainName;
        this.roleType = roleType;
        this.modified = DateUtil.formatDate(modified);
    }

    public long getDomainId() {
        return domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getRoleType() {
        return roleType;
    }

    public String getModified(){
        return modified;
    }


    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.DEFAULT_STYLE);
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UserDomain that = (UserDomain) o;

        if (domainId != that.domainId) return false;
        if (!domainName.equals(that.domainName)) return false;
        if (!roleType.equals(that.roleType)) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (int) (domainId ^ (domainId >>> 32));
        result = 31 * result + domainName.hashCode();
        result = 31 * result + roleType.hashCode();
        return result;
    }

    public int compareTo(UserDomain o) {
        return this.getDomainName().compareTo(o.getDomainName());
    }
}
