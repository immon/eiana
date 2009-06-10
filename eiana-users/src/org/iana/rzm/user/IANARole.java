package org.iana.rzm.user;

import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.Entity;

/**
 * @author Patrycja Wegrzynowicz
 */
@Entity
public class IANARole extends Role implements Cloneable {

    public enum IANAType implements Role.Type {
        IANA
    }

    public boolean isAdmin() {
        return true;
    }

    public Type getType() {
        return IANAType.IANA;
    }

    public void setType(Type type) {
        CheckTool.checkNull(type, "type");
        if (!(type instanceof IANAType))
            throw new IllegalArgumentException("type");
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
