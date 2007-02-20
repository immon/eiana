package org.iana.rzm.log;

import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 */
public class Log {
    private String type;
    private Modification modification;
    private Timestamp created;
    private Identity identity;

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Modification getModification() {
        return modification;
    }

    public void setModification(Modification modification) {
        this.modification = modification;
    }

    public Timestamp getCreated() {
        return created;
    }

    public void setCreated(Timestamp created) {
        this.created = created;
    }

    public Identity getIdentity() {
        return identity;
    }

    public void setIdentity(Identity identity) {
        this.identity = identity;
    }
}
