package org.iana.rzm.facade.system.trans.vo;

import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.facade.common.TrackDataVO;
import org.iana.rzm.facade.system.trans.vo.changes.TransactionActionVO;
import org.iana.rzm.facade.user.SystemRoleVO;

import java.sql.Timestamp;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    private List<String> tokens;
    private boolean redelegation;
    private String submitterEmail;
    //private Set<SystemRoleVO.SystemType> confirmations = new HashSet<SystemRoleVO.SystemType>();
    private Set<ConfirmationVO> confirmations = new HashSet<ConfirmationVO>();
    private String comment;
    private String stateMessage;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDomainName() {
        return domainName;
    }

    public void setDomainName(String domainName) {
        this.domainName = domainName;
    }

    public List<TransactionActionVO> getDomainActions() {
        return domainActions;
    }

    public void setDomainActions(List<TransactionActionVO> domainActions) {
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

    public List<String> getTokens() {
        return tokens;
    }

    public void setTokens(List<String> tokens) {
        this.tokens = tokens;
    }

    public boolean isRedelegation() {
        return redelegation;
    }

    public void setRedelegation(boolean redelegation) {
        this.redelegation = redelegation;
    }

    public String getSubmitterEmail() {
        return submitterEmail;
    }

    public void setSubmitterEmail(String submitterEmail) {
        this.submitterEmail = submitterEmail;
    }

    public boolean acConfirmed() {
        for (ConfirmationVO conf : confirmations)
            if (conf.getRole() == SystemRoleVO.SystemType.AC && conf.isConfirmed())
                return true;
        return false;
    }

    public boolean tcConfirmed() {
        for (ConfirmationVO conf : confirmations)
            if (conf.getRole() == SystemRoleVO.SystemType.TC && conf.isConfirmed())
                return true;
        return false;
    }

    public boolean soConfirmed() {
        for (ConfirmationVO conf : confirmations)
            if (conf.getRole() == SystemRoleVO.SystemType.SO && conf.isConfirmed())
                return true;
        return false;
    }

    public Set<ConfirmationVO> getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Set<ConfirmationVO> confirmations) {
        this.confirmations = confirmations;
    }

    public void addConfirmation(ConfirmationVO confirmation) {
        if (confirmation == null) confirmations = new HashSet<ConfirmationVO>();
        confirmations.add(confirmation);
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getStateMessage() {
        return stateMessage;
    }

    public void setStateMessage(String stateMessage) {
        this.stateMessage = stateMessage;
    }
}
