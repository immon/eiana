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
}
