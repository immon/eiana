package org.iana.rzm.common;

import java.sql.Timestamp;

/**
 * @author Jakub Laszkiewicz
 */
public interface TrackedObject {
    Timestamp getCreated();
    Timestamp getModified();
    String getCreatedBy();
    String getModifiedBy();
}
