package org.iana.rzm.facade.user;

/**
 * @author Patrycja Wegrzynowicz
 */
public class RoleVO {

    public interface Type {}

    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;        
    }
}
