package org.iana.rzm.user;

import org.iana.rzm.common.validators.CheckTool;

public class AdminUser extends User {

    public static enum Type implements UserType {
        IANA_STAFF,
        GOV_OVERSIGHT,
        ZONE_PUBLISHER
    }

    private Type type;

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        CheckTool.checkNull(type, "type");
        this.type = type;
    }
}
