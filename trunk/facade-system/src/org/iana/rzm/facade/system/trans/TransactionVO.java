package org.iana.rzm.facade.system.trans;

import org.iana.rzm.facade.common.TrackDataVO;
import org.iana.rzm.common.TrackedObject;

import java.sql.Timestamp;
import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class TransactionVO extends TrackDataVO implements TrackedObject {

    private Long transactionID;
    private Long ticketID;
    private String name;
    private String domainName;
    private List<TransactionActionVO> domainActions;
    private TransactionStateVO state;
    private List<TransactionStateLogEntryVO> stateLog;
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

    public List<TransactionStateLogEntryVO> getStateLog() {
        return stateLog;
    }

    public void setStateLog(List<TransactionStateLogEntryVO> stateLog) {
        this.stateLog = stateLog;
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

    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        TransactionVO that = (TransactionVO) o;

        if (domainActions != null ? !domainActions.equals(that.domainActions) : that.domainActions != null)
            return false;
        if (domainName != null ? !domainName.equals(that.domainName) : that.domainName != null) return false;
        if (end != null ? !end.equals(that.end) : that.end != null) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (start != null ? !start.equals(that.start) : that.start != null) return false;
        if (state != null ? !state.equals(that.state) : that.state != null) return false;
        if (ticketID != null ? !ticketID.equals(that.ticketID) : that.ticketID != null) return false;
        if (transactionID != null ? !transactionID.equals(that.transactionID) : that.transactionID != null)
            return false;

        return true;
    }

    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (transactionID != null ? transactionID.hashCode() : 0);
        result = 31 * result + (ticketID != null ? ticketID.hashCode() : 0);
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (domainName != null ? domainName.hashCode() : 0);
        result = 31 * result + (domainActions != null ? domainActions.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (start != null ? start.hashCode() : 0);
        result = 31 * result + (end != null ? end.hashCode() : 0);
        return result;
    }

    public Long getObjId() {
        return transactionID;
    }
}
