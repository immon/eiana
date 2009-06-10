package org.iana.rzm.user;

import org.iana.rzm.common.validators.CheckTool;

import javax.persistence.Entity;

@Entity
public class GovRole extends Role implements Cloneable {

    public enum GovType implements Role.Type {
        GOV_OVERSIGHT
    }

    final public GovType getType() {
        return GovType.GOV_OVERSIGHT;
    }

    final public void setType(Role.Type type) {
        CheckTool.checkNull(type, "type");
        if (!(type instanceof GovType))
            throw new IllegalArgumentException("type");
    }

    final public boolean isAdmin() {
        return true;
    }

    public boolean isRoot() {
        return false;
    }

    public boolean equals(Object object) {
        return object instanceof GovRole && super.equals(object);
    }

    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
}
