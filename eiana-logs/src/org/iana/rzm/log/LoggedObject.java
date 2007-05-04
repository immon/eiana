package org.iana.rzm.log;

/**
 * @author Patrycja Wegrzynowicz
 */
public class LoggedObject {
    private long objectId;
    private String objectType;

    public LoggedObject(long objectId, String objectType) {
        this.objectId = objectId;
        this.objectType = objectType;
    }

    public long getObjectId() {
        return objectId;
    }

    public String getObjectType() {
        return objectType;
    }
}
