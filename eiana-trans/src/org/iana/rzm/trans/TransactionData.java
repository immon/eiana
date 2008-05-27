package org.iana.rzm.trans;

import org.hibernate.annotations.Cascade;
import org.iana.notifications.PNotification;
import org.iana.objectdiff.Change;
import org.iana.objectdiff.CollectionChange;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.SimpleChange;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.confirmation.contact.ContactConfirmations;
import org.iana.rzm.trans.confirmation.usdoc.USDoCConfirmation;
import org.iana.rzm.trans.epp.info.EPPChangeStatus;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.*;

/**
 * @author Jakub Laszkiewicz
 */
@Entity
public class TransactionData {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long objId;

    @Basic
    private Long ticketID;

    @OneToMany(cascade=CascadeType.REMOVE)
    @Cascade(org.hibernate.annotations.CascadeType.DELETE_ORPHAN)
    @JoinColumn(name="td_id")
    private Set<PNotification> notifications = new HashSet<PNotification>();

    @ManyToOne(cascade = {})
    @JoinColumn(name = "currentDomain_objId")
    private Domain currentDomain;
    
    @ManyToMany(cascade = {})
    @JoinTable(name = "impacted_domains",
        inverseJoinColumns = @JoinColumn(name = "impacted_domain_id"))
    private Set<Domain> impactedDomains = new HashSet<Domain>();

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "domainChange_objId")
    private ObjectChange domainChange;

    @OneToMany(cascade = CascadeType.ALL)
    @MapKey(name = "stateName")
    @Cascade(value = {org.hibernate.annotations.CascadeType.ALL,
            org.hibernate.annotations.CascadeType.DELETE_ORPHAN})
    private Map<TransactionState.Name, ContactConfirmations> contactConfirmations =
            new HashMap<TransactionState.Name, ContactConfirmations>();

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "TransactionData_transactionStateLog",
            inverseJoinColumns = @JoinColumn(name = "transactionStateLog_objId"))
    private List<TransactionStateLogEntry> stateLog = new ArrayList<TransactionStateLogEntry>();

    @Embedded
    protected TrackData trackData = new TrackData();

    @Basic
    private String identityName;

    @Basic
    private boolean redelegation; // contact redelegation!

    @Basic
    private String submitterEmail;

    @Basic
    @Column(length = 4096)
    private String comment;

    private static final int MAX_LEN = 4095;

    @Basic
    @Column(length = 4096)
    private String stateMessage;

    @Basic
    private String eppRequestId;

    @Basic
    @Column(length = 4096)
    private String eppReceipt;

    @Basic
    private EPPChangeStatus eppStatus;

    @Basic
    private int retries;

    @Basic
    private String serialNumber;

    @Lob
    private String technicalErrors;

    @Basic
    @Column(length = 4096)
    private String usdocNotes;

    @Embedded
    private USDoCConfirmation confirmation;

    public Long getObjId() {
        return objId;
    }

    public void setObjId(Long objId) {
        this.objId = objId;
    }

    public Long getTicketID() {
        return ticketID;
    }

    public void setTicketID(Long ticketID) {
        this.ticketID = ticketID;
    }

    public Domain getCurrentDomain() {
        return currentDomain;
    }

    public void setCurrentDomain(Domain currentDomain) {
        this.currentDomain = currentDomain;
    }

    public ObjectChange getDomainChange() {
        return domainChange;
    }

    public void setDomainChange(ObjectChange domainChange) {
        this.domainChange = domainChange;
    }

    public List<TransactionStateLogEntry> getStateLog() {
        return stateLog;
    }

    public void addStateLogEntry(TransactionStateLogEntry entry) {
        stateLog.add(entry);
        updateModified();
    }

    public void updateModified() {
        getTrackData().setModified(new Timestamp(System.currentTimeMillis()));
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

    public TrackData getTrackData() {
        return trackData;
    }

    public ContactConfirmations getContactConfirmations(TransactionState.Name stateName) {
        return contactConfirmations.get(stateName);
    }

    public void setContactConfirmations(ContactConfirmations conf) {
        this.contactConfirmations.put(conf.getStateName(), conf);
    }

    public String getIdentityName() {
        return identityName;
    }

    public void setIdentityName(String identityName) {
        this.identityName = identityName;
    }

    public void clearIdentityName() {
        this.identityName = null;
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

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }


    public int getEPPRetries() {
        return retries;
    }

    public void setEPPRetries(int retries) {
        this.retries = retries;
    }

    public String getStateMessage() {
        return stateMessage;
    }

    public void setStateMessage(String stateMessage) {
        if (stateMessage != null && stateMessage.length() > MAX_LEN) {
            stateMessage = stateMessage.substring(0, MAX_LEN);
        }
        this.stateMessage = stateMessage;
    }

    public String getEppRequestId() {
        return eppRequestId;
    }

    public void setEppRequestId(String eppRequestId) {
        this.eppRequestId = eppRequestId;
    }

    public String getEppReceipt() {
        return eppReceipt;
    }

    public void setEppReceipt(String eppReceipt) {
        this.eppReceipt = eppReceipt;
    }

    public boolean isNameServerChange() {
        return isChange("nameServers");
    }

    public boolean isAdminContactChange() {
        return isChange("adminContact");
    }

    public boolean isTechContactChange() {
        return isChange("techContact");
    }

    public boolean isSupportingChange() {
        return isChange("supportingChange");
    }

    private boolean isChange(String change) {
        if (domainChange == null) return false;
        Map<String, Change> fields = domainChange.getFieldChanges();
        return fields.containsKey(change);
    }

    public Set<String> getAddedOrUpdatedNameServers() {
        Set<String> ret = new HashSet<String>();
        if (!isNameServerChange()) return ret;
        Map<String, Change> fields = domainChange.getFieldChanges();
        CollectionChange nsChange = (CollectionChange) fields.get("nameServers");
        for (Change change : nsChange.getModified()) {
            ObjectChange obj = (ObjectChange) change;
            ret.add(obj.getId());
        }
        for (Change change : nsChange.getAdded()) {
            ObjectChange obj = (ObjectChange) change;
            if (obj.isModification()) {
                ret.add(obj.getId());
            }
        }
        return ret;
    }

    public boolean isDatabaseChange() {
        if (domainChange == null) return false;
        Map<String, Change> fields = domainChange.getFieldChanges();
        int size = fields.size();
        if (fields.containsKey("nameServers")) --size;
        return size > 0;
    }

    public void setupUSDoCConfirmation() {
        this.confirmation = new USDoCConfirmation(isDatabaseChange(), isNameServerChange());
    }

    public USDoCConfirmation getUSDoCConfirmation() {
        return confirmation;
    }


    public String getUsdocNotes() {
        return usdocNotes;
    }

    public void setUsdocNotes(String usdocNotes) {
        this.usdocNotes = usdocNotes;
    }

    public String getStateStartDate(TransactionState.Name stateName) {
        TransactionState state = getTransactionState(stateName);
        return (state == null) ? "" : String.valueOf(state.getStart().getTime());
    }

    public String getStateEndDate(TransactionState.Name stateName) {
        TransactionState state = getTransactionState(stateName);
        return (state == null) ? "" : String.valueOf(state.getEnd().getTime());
    }

    private TransactionState getTransactionState(TransactionState.Name stateName) {
        for (TransactionStateLogEntry logEntry : getStateLog()) {
            if (logEntry.getState().getName().equals(stateName))
                return logEntry.getState();
        }
        return null;
    }

    public String getSponsoringOrgChangedEmail() {
        return getChangedEmail("supportingOrg");
    }

    public String getAdminChangedEmail() {
        return getChangedEmail("adminContact");        
    }

    public String getTechChangedEmail() {
        return getChangedEmail("techContact");
    }

    private String getChangedEmail(String contact) {
        ObjectChange domainChange = getDomainChange();
        if (domainChange != null) {
            ObjectChange contactChange = (ObjectChange) domainChange.getFieldChanges().get(contact);
            if (contactChange != null) {
                SimpleChange emailChange = (SimpleChange) contactChange.getFieldChanges().get("email");
                if (emailChange != null) return emailChange.getNewValue();
            }
        }
        return null;
    }

    public void addNotification(PNotification notification) {
        if (notification != null && notification.isPersistent()) {
            notifications.add(notification);
        }
    }

    public Set<PNotification> getNotifications() {
        return notifications;
    }

    public void resetConfirmation() {
        this.contactConfirmations = null;
    }

    public Set<Domain> getImpactedDomains() {
        return impactedDomains;
    }

    public void setImpactedDomains(Set<Domain> impactedDomains) {
        CheckTool.checkNull(impactedDomains, "impacted domains");
        this.impactedDomains = impactedDomains;
    }

    EPPChangeStatus getEppStatus() {
        return eppStatus;
    }

    void setEppStatus(EPPChangeStatus eppStatus) {
        this.eppStatus = eppStatus;
    }


    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getTechnicalErrors() {
        return technicalErrors;
    }

    public void setTechnicalErrors(String technicalErrors) {
        this.technicalErrors = technicalErrors;
    }
}
