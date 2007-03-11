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
}
