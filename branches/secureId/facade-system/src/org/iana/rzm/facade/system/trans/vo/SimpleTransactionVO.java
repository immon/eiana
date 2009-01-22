package org.iana.rzm.facade.system.trans.vo;

import org.iana.rzm.facade.common.TrackDataVO;
import org.iana.rzm.facade.common.Trackable;

import java.sql.Timestamp;

/**
 * A simplified version of TransactionVO used with lists of transactions.
 * 
 * @author Patrycja Wegrzynowicz
 */
public class SimpleTransactionVO implements Trackable {

    private Long transactionID;
    private Long ticketID;
    private String name;
    private String domainName;
    private TrackDataVO trackData = new TrackDataVO();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Long transactionID) {
        this.transactionID = transactionID;
    }

    public Long getTicketID() {
        return ticketID;
    }

    public void setTicketID(Long ticketID) {
        this.ticketID = ticketID;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public Timestamp getCreated() {
        return trackData.getCreated();
    }

    public Timestamp getModified() {
        return trackData.getModified();
    }

    public String getCreatedBy() {
        return trackData.getCreatedBy();
    }

    public String getModifiedBy() {
        return trackData.getModifiedBy();
    }
}
