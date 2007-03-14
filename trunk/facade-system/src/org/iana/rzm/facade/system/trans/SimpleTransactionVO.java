package org.iana.rzm.facade.system.trans;

import org.iana.rzm.facade.common.TrackDataVO;
import org.iana.rzm.facade.common.Trackable;
import org.iana.rzm.facade.system.domain.SimpleDomainVO;

import java.sql.Timestamp;

/**
 * A simplified version of TransactionVO used with lists of transactions.
 * 
 * @author Patrycja Wegrzynowicz
 */
public class SimpleTransactionVO implements Trackable {

    private String name;
    private SimpleDomainVO domain;
    private TrackDataVO data;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public SimpleDomainVO getDomain() {
        return domain;
    }

    public void setDomain(SimpleDomainVO domain) {
        this.domain = domain;
    }

    public Timestamp getCreated() {
        return data.getCreated();
    }

    public void setCreated(Timestamp created) {
        data.setCreated(created);
    }

    public Timestamp getModified() {
        return data.getModified();
    }

    public void setModified(Timestamp modified) {
        data.setModified(modified);
    }

    public String getModifiedBy() {
        return data.getModifiedBy();
    }

    public void setModifiedBy(String modifiedBy) {
        data.setModifiedBy(modifiedBy);
    }

    public void setCreatedBy(String createdBy) {
        data.setCreatedBy(createdBy);
    }

    public String getCreatedBy() {
        return data.getCreatedBy();
    }
}
