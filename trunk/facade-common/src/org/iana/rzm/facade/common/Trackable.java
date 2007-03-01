package org.iana.rzm.facade.common;

import java.sql.Timestamp;

/**
 * @author Patrycja Wegrzynowicz
 */
public interface Trackable {

    public Timestamp getCreated();

    public Timestamp getModified();

    public String getCreatedBy();

    public String getModifiedBy();

}
