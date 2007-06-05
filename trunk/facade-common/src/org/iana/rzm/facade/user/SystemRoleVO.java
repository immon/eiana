package org.iana.rzm.facade.user;

/**
 * @author Patrycja Wegrzynowicz
 * @author Marcin Zajaczkowski
 */
public class SystemRoleVO extends RoleVO {

    public enum SystemType implements RoleVO.Type {
        AC, TC, SO
    }
    
    private String name;
    private boolean notify;
    private boolean acceptFrom;
    private boolean mustAccept;
    private boolean accessToDomain;


    public SystemRoleVO() {
    }

    public SystemRoleVO(SystemType type) {
        super(type);
    }

    public SystemType getType() {
        return (SystemType) super.getType();
    }

    public void setType(SystemType type) {
        super.setType(type);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isNotify() {
        return notify;
    }

    public void setNotify(boolean notify) {
        this.notify = notify;
    }

    public boolean isAcceptFrom() {
        return acceptFrom;
    }

    public void setAcceptFrom(boolean acceptFrom) {
        this.acceptFrom = acceptFrom;
    }

    public boolean isMustAccept() {
        return mustAccept;
    }

    public void setMustAccept(boolean mustAccept) {
        this.mustAccept = mustAccept;
    }

    public boolean isAdmin() {
        return false;
    }

    public boolean isAccessToDomain() {
        return accessToDomain;
    }

    public void setAccessToDomain(boolean accessToDomain) {
        this.accessToDomain = accessToDomain;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        SystemRoleVO that = (SystemRoleVO) o;

        if (acceptFrom != that.acceptFrom) return false;
        if (accessToDomain != that.accessToDomain) return false;
        if (mustAccept != that.mustAccept) return false;
        if (notify != that.notify) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (notify ? 1 : 0);
        result = 31 * result + (acceptFrom ? 1 : 0);
        result = 31 * result + (mustAccept ? 1 : 0);
        result = 31 * result + (accessToDomain ? 1 : 0);
        return result;
    }
}
