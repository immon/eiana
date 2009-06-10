package org.iana.rzm.web.model;

import org.apache.commons.lang.builder.*;
import org.iana.rzm.web.util.*;

import java.io.*;
import java.util.*;

public class UserDomain implements Serializable, Comparable<UserDomain> {

    private long domainId;
    private String domainName ;
    private String modified;
    private List<String>roleTypes;


    public UserDomain(long domainId, String domainName, String roleType, Date modified) {
        this.domainId = domainId;
        this.domainName = domainName;
        this.modified = DateUtil.formatDate(modified);
        roleTypes = new ArrayList<String>();
        addRole(roleType);
    }

    public long getDomainId() {
        return domainId;
    }

    public String getDomainName() {
        return domainName;
    }

    public String getRoleType() {
        if(roleTypes.size() == 1){
            return roleTypes.get(0);
        }

        StringBuilder builder = new StringBuilder();
        int index = 0;
        for (String type : roleTypes) {
            builder.append(type);
            if(index < roleTypes.size() - 1){
                builder.append(",");
            }
            index++;
        }
        return builder.toString();
    }

    public String getModified(){
        return modified;
    }


    public void addRole(String roleType){
        if(!roleTypes.contains(roleType)){
            roleTypes.add(roleType);
        }
    }

    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.DEFAULT_STYLE);
    }


    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        UserDomain that = (UserDomain) o;

        if (domainId != that.domainId) {
            return false;
        }
        if (domainName != null ? !domainName.equals(that.domainName) : that.domainName != null) {
            return false;
        }
        if (modified != null ? !modified.equals(that.modified) : that.modified != null) {
            return false;
        }
        if (roleTypes != null ? !roleTypes.equals(that.roleTypes) : that.roleTypes != null) {
            return false;
        }

        return true;
    }

    public int hashCode() {
        int result;
        result = (int) (domainId ^ (domainId >>> 32));
        result = 31 * result + (domainName != null ? domainName.hashCode() : 0);
        result = 31 * result + (modified != null ? modified.hashCode() : 0);
        result = 31 * result + (roleTypes != null ? roleTypes.hashCode() : 0);
        return result;
    }

    public int compareTo(UserDomain o) {
        return this.getDomainName().compareTo(o.getDomainName());
    }
}
