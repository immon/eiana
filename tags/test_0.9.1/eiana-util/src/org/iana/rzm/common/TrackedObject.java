package org.iana.rzm.common;

import java.sql.Timestamp;

/**
 * @author Jakub Laszkiewicz
 */
public interface TrackedObject {

    Long getObjId();
    
    Timestamp getCreated();

    Timestamp getModified();

    String getCreatedBy();

    String getModifiedBy();

    void setCreated(Timestamp created);

    void setModified(Timestamp modified);

    void setCreatedBy(String userName);

    void setModifiedBy(String userName);
}
