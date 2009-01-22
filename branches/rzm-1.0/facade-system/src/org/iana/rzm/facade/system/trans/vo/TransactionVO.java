package org.iana.rzm.facade.system.trans.vo;

import org.iana.rzm.common.*;
import org.iana.rzm.facade.common.*;
import org.iana.rzm.facade.system.trans.vo.changes.*;
import org.iana.rzm.facade.user.*;

import java.sql.*;
import java.util.*;

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
    private Set<ConfirmationVO> confirmations = new HashSet<ConfirmationVO>();
    private Set<ConfirmationVO> impactedPartyConfirmations = new HashSet<ConfirmationVO>();
    private Set<String> impactedDomains = new HashSet<String>();
    private boolean specialReviewInvolved;
    private String comment;
    private String stateMessage;
    private String usdocNotes;
    private String eppStatus;
    private String technicalErrors;

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

    public List<String> getTokens(SystemRoleVO.SystemType type) {
        List<String> ret = new ArrayList<String>();
        for (ConfirmationVO conf : getLastConfirmations()) {
            if (conf.getRole() == type) ret.add(conf.getToken());
        }
        return ret;
    }

    public Set<ConfirmationVO> getLastConfirmations() {
        return impactedPartyConfirmations.size() > 0 ? impactedPartyConfirmations : confirmations;
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
        return confirmed(SystemRoleVO.SystemType.AC);
    }

    public boolean tcConfirmed() {
        return confirmed(SystemRoleVO.SystemType.TC);
    }

    public boolean soConfirmed() {
        return confirmed(SystemRoleVO.SystemType.SO);
    }

    private boolean confirmed(SystemRoleVO.SystemType role) {
        boolean ret = false;
        for (ConfirmationVO conf : confirmations)
            if (conf.getRole() == role) {
                if (!conf.isConfirmed()) return false;
                else ret = true;
            }
        return ret;
    }

    public Set<ConfirmationVO> getConfirmations() {
        return confirmations;
    }

    public void setConfirmations(Set<ConfirmationVO> confirmations) {
        this.confirmations = confirmations;
    }

    public void addConfirmation(ConfirmationVO confirmation) {
        if (confirmation == null) return;
        if (confirmations == null) confirmations = new HashSet<ConfirmationVO>();
        confirmations.add(confirmation);
    }

    public Set<ConfirmationVO> getContactConfirmations() {
        return getConfirmations();
    }

    public void addContactConfirmation(ConfirmationVO confirmation) {
        addConfirmation(confirmation);
    }

    public Set<ConfirmationVO> getImpactedPartyConfirmations() {
        return impactedPartyConfirmations;
    }

    public void setImpactedPartyConfirmations(Set<ConfirmationVO> impactedPartyConfirmations) {
        this.impactedPartyConfirmations = impactedPartyConfirmations;
    }

    public void addImpactedPartyConfirmation(ConfirmationVO confirmation) {
        if (confirmation == null) return;
        if (impactedPartyConfirmations == null) impactedPartyConfirmations = new HashSet<ConfirmationVO>();
        impactedPartyConfirmations.add(confirmation);
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

    public String getUsdocNotes() {
        return usdocNotes;
    }

    public void setUsdocNotes(String usdocNotes) {
        this.usdocNotes = usdocNotes;
    }

    public Set<String> getImpactedDomains() {
        return impactedDomains;
    }

    public void setImpactedDomains(Set<String> impactedDomains) {
        this.impactedDomains = impactedDomains;
    }

    public void addImpactedDomain(String domainName) {
        if (this.impactedDomains == null) this.impactedDomains = new HashSet<String>();
        this.impactedDomains.add(domainName);
    }

    public boolean isSpecialReviewInvolved() {
        return this.specialReviewInvolved;
    }

    public void setSpecialReviewInvolved(boolean specialReviewInvolved) {
        this.specialReviewInvolved = specialReviewInvolved;
    }

    public Set<String> getAffectedDomains() {
        Set<String> ret = new HashSet<String>();
        if (impactedDomains != null) ret.addAll(impactedDomains);
        ret.add(domainName);
        return ret;
    }

    public boolean isGlueChange() {
        return impactedDomains != null && impactedDomains.size() > 0;
    }

    public String getEppStatus() {
        return eppStatus;
    }

    public void setEppStatus(String eppStatus) {
        this.eppStatus = eppStatus;
    }

    public void setTechnicalErrors(String technicalErrors) {
        this.technicalErrors = technicalErrors;
    }

    public String getTechnicalErrors(){
        return technicalErrors;
    }
}
