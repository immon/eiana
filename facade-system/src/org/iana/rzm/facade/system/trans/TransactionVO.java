package org.iana.rzm.facade.system.trans;

import org.iana.rzm.facade.common.TrackDataVO;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionVO extends TrackDataVO {

    private Long transactionID;
    private Long ticketID;
    private String name;
    private String domainName;
    private List<TransactionActionVO> domainActions;
    private TransactionStateVO state;
    private Timestamp start;
    private Timestamp end;

    public Long getTransactionID() {
        return transactionID;
    }

    void setTransactionID(Long transactionID) {
        this.transactionID = transactionID;
    }

    public Long getTicketID() {
        return ticketID;
    }

    void setTicketID(Long ticketID) {
        this.ticketID = ticketID;
    }

    public String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    public String getDomainName() {
        return domainName;
    }

    void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public List<TransactionActionVO> getDomainActions() {
        return domainActions;
    }

    void setDomainActions(List<TransactionActionVO> domainActions) {
        this.domainActions = domainActions;
    }

    public TransactionStateVO getState() {
        return state;
    }

    public void setState(TransactionStateVO state) {
        this.state = state;
    }

    public Timestamp getStart() {
        return start;
    }

    public void setStart(Timestamp start) {
        this.start = start;
    }

    public Timestamp getEnd() {
        return end;
    }

    public void setEnd(Timestamp end) {
        this.end = end;
    }
}
