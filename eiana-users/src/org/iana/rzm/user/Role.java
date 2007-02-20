package org.iana.rzm.user;

import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.Name;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.common.exceptions.InvalidNameException;

public class Role extends TrackedObject {

    public static enum Type implements UserType {
        AC, TC, SO
    }

    private Name name;
    private Type type;
    private boolean notify;
    private boolean acceptFrom;
    private boolean mustAccept;

    final public String getName() {
        return name == null ? null : name.getName();
    }

    final public void setName(String name) throws InvalidNameException {
        this.name = new Name(name);
    }

    final public Type getType() {
        return type;
    }

    final public void setType(Type type) {
        CheckTool.checkNull(type, "type");
        this.type = type;
    }

    final public boolean isNotify() {
        return notify;
    }

    final public void setNotify(boolean notify) {
        this.notify = notify;
    }

    final public boolean isAcceptFrom() {
        return acceptFrom;
    }

    final public void setAcceptFrom(boolean acceptFrom) {
        this.acceptFrom = acceptFrom;
    }

    final public boolean isMustAccept() {
        return mustAccept;
    }

    final public void setMustAccept(boolean mustAccept) {
        this.mustAccept = mustAccept;
    }

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Role role = (Role) o;

        if (name != null ? !name.equals(role.name) : role.name != null) return false;
        if (type != role.type) return false;

        return true;
    }

    public int hashCode() {
        int result;
        result = (name != null ? name.hashCode() : 0);
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }
}
