package org.iana.rzm.facade.system;

import org.iana.rzm.facade.common.TrackDataVO;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionVO extends TrackDataVO {

    private Long transactionID;
    private Long rtID;
    private String name;
    private IDomainVO currentDomain;
    private List<TransactionActionVO> actions;
    private TransactionStateVO state;
    private Timestamp start;
    private Timestamp end;

    public Long getTransactionID() {
        return transactionID;
    }

    public void setTransactionID(Long transactionID) {
        this.transactionID = transactionID;
    }

    public Long getRtID() {
        return rtID;
    }

    public void setRtID(Long rtID) {
        this.rtID = rtID;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public IDomainVO getCurrentDomain() {
        return currentDomain;
    }

    public void setCurrentDomain(IDomainVO currentDomain) {
        this.currentDomain = currentDomain;
    }

    public List<TransactionActionVO> getActions() {
        return actions;
    }

    public void setActions(List<TransactionActionVO> actions) {
        this.actions = actions;
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
