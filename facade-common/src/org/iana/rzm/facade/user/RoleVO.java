package org.iana.rzm.facade.user;

/**
 * @author Patrycja Wegrzynowicz
 * @author Marcin Zajaczkowski
 */
public class RoleVO {

    public interface Type {}

    private Type type;


    public RoleVO() {
    }

    public RoleVO(Type type) {
        this.type = type;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;        
    }

    /**
     * Tells if role the Admin role
     *
     * Could be done by visitor, but I don't see a need to use it here 
     *
     * @return is role is the Admin role
     */
    public boolean isAdmin() {
        return false;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        RoleVO roleVO = (RoleVO) o;

        if (type != null ? !type.equals(roleVO.type) : roleVO.type != null) return false;

        return true;
    }

    public int hashCode() {
        return (type != null ? type.hashCode() : 0);
    }
}
