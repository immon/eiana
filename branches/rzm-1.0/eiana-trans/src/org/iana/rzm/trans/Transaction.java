package org.iana.rzm.trans;

import org.apache.log4j.Logger;
import org.iana.notifications.PNotification;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.change.TransactionChangeType;
import org.iana.rzm.trans.confirmation.AlreadyAcceptedByUser;
import org.iana.rzm.trans.confirmation.Confirmation;
import org.iana.rzm.trans.confirmation.NotAcceptableByUser;
import org.iana.rzm.trans.confirmation.contact.ContactConfirmations;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.confirmation.usdoc.USDoCConfirmation;
import org.iana.rzm.trans.dao.ProcessDAO;
import org.iana.rzm.trans.epp.info.EPPChangeStatus;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import java.sql.Timestamp;
import java.util.*;

/**
 * This class represents a domain modification transaction.
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class Transaction implements TrackedObject {

    private static final Logger logger = Logger.getLogger(Transaction.class);

    public static final String TRANSACTION_DATA = "TRANSACTION_DATA";

    public static final String EXCEPTION = "TRANSITION_EXCEPTION";

    private ProcessInstance pi;

    private ProcessDAO processDAO;

    private TransactionData data;

    public Transaction(ProcessInstance pi) {
        this(pi, null);
    }

    public Transaction(ProcessInstance pi, ProcessDAO dao) {
        CheckTool.checkNull(pi, "process instance");
        this.pi = pi;
        if (!this.pi.getContextInstance().hasVariable(TRANSACTION_DATA)) {
            this.pi.getContextInstance().setVariable(TRANSACTION_DATA, new TransactionData());
        }
        this.data = (TransactionData) pi.getContextInstance().getVariable(TRANSACTION_DATA);
        this.processDAO = dao;
    }

    public TransactionData getData() {
        return data;
    }

    private TrackData getTrackData() {
        return getData().getTrackData();
    }

    public Long getObjId() {
        return getTransactionID();
    }

    public Long getTransactionID() {
        return pi.getId();
    }

    public Long getTicketID() {
        return getData().getTicketID();
    }

    public void setTicketID(Long ticketID) {
        getData().setTicketID(ticketID);
    }

    public String getName() {
        return pi.getProcessDefinition().getName();
    }

    public Domain getCurrentDomain() {
        return getData().getCurrentDomain();
    }

    public void setCurrentDomain(Domain currentDomain) {
        getData().setCurrentDomain(currentDomain);
    }

    public ObjectChange getDomainChange() {
        return getData().getDomainChange();
    }

    public void setDomainChange(ObjectChange change) {
        getData().setDomainChange(change);
    }

    public TransactionState getState() {
        TransactionState ts = new TransactionState();
        try {
            Token token = pi.getRootToken();
            Node node = token.getNode();
            if (node != null) {
                ts.setName(node.getName());
                ts.setStart(token.getNodeEnter());
                for (Object o : node.getLeavingTransitions()) {
                    Transition transition = (Transition) o;
                    ts.addAvailableTransition(new StateTransition(transition.getName()));
                }
            }
            return ts;
        } catch (IllegalArgumentException e) {
            // todo: do something with that setName!!!
            return ts;
        }
    }

    public List<TransactionStateLogEntry> getStateLog() {
        return getData().getStateLog();
    }

    public Timestamp getStart() {
        Timestamp result = new Timestamp(pi.getStart().getTime());
        // when process instance is persisted in db, nanos are missing
        result.setNanos(0);
        return result;
    }

    public void setStart(Timestamp start) {
        pi.setStart(start);
    }

    public Timestamp getEnd() {
        if (pi.getEnd() == null) return null;
        Timestamp result = new Timestamp(pi.getEnd().getTime());
        // when process instance is persisted in db, nanos are missing
        result.setNanos(0);
        return result;
    }

    public void setEnd(Timestamp end) {
        pi.setEnd(end);
    }

    public Timestamp getCreated() {
        return getTrackData().getCreated();
    }

    public Timestamp getModified() {
        return getTrackData().getModified();
    }

    public String getCreatedBy() {
        return getTrackData().getCreatedBy();
    }

    public String getModifiedBy() {
        return getTrackData().getModifiedBy();
    }

    public void setCreated(Timestamp created) {
        getTrackData().setCreated(created);
    }

    public void setModified(Timestamp modified) {
        getTrackData().setModified(modified);
    }

    public void setCreatedBy(String userName) {
        getTrackData().setCreatedBy(userName);
    }

    public void setModifiedBy(String userName) {
        getTrackData().setModifiedBy(userName);
    }

    public synchronized void systemAccept() throws TransactionException {
        setModified(new Timestamp(new Date().getTime()));
        setModifiedBy("SYSTEM");
        processDAO.signal(pi, StateTransition.ACCEPT);
    }

    public synchronized void accept(RZMUser user) throws TransactionException {
        getData().setIdentityName(user.getLoginName());
        processDAO.signal(pi, StateTransition.ACCEPT);
    }

    public synchronized void reject(RZMUser user) throws TransactionException {
        getData().setIdentityName(user.getLoginName());
        processDAO.signal(pi, StateTransition.REJECT);
    }

    public synchronized void accept(String acceptToken) throws TransactionException {
        try {
            ContactIdentity user = new ContactIdentity(acceptToken);
            Confirmation confirmation = getContactConfirmations();
            if (confirmation == null) {
                throw new UserConfirmationNotExpected();
            }
            if (!confirmation.accept(user)) {
                return;
            }
            getData().setIdentityName("AC/TC");
            processDAO.signal(pi, StateTransition.ACCEPT);
        } catch (AlreadyAcceptedByUser e) {
            throw new UserAlreadyAccepted(e);
        } catch (NotAcceptableByUser e) {
            throw new UserConfirmationNotExpected(e);
        }
    }

    public synchronized void reject(String acceptToken) throws TransactionException {
        ContactIdentity user = new ContactIdentity(acceptToken);
        Confirmation confirmation = getContactConfirmations();
        if (confirmation == null) {
            throw new UserConfirmationNotExpected();
        }
        if (confirmation.isAcceptableBy(user)) {
            getData().setIdentityName("AC/TC");
            processDAO.signal(pi, StateTransition.REJECT);
        } else
            throw new UserConfirmationNotExpected();
    }

    public synchronized void usdocRejected() throws TransactionException {
        TransactionState.Name state = getState().getName();
        if (state == TransactionState.Name.PENDING_ZONE_INSERTION) {
            exception("status mismatch: usdoc accepted but verisign signaled docRejected");
        }
    }

    public synchronized void usdocAccepted() throws TransactionException {
        TransactionState.Name state = getState().getName();
        if (state == TransactionState.Name.REJECTED) {
            exception("status mismatch: usdoc rejected but verisign signalled docAccepted");
        }
    }

    public synchronized void generated() throws TransactionException {
        TransactionState.Name state = getState().getName();
        if (state == TransactionState.Name.PENDING_ZONE_INSERTION) {
            systemAccept();
        } else if (state == TransactionState.Name.REJECTED) {
            exception("status mismatch: usdoc rejected but verisign signalled generated");            
        } else {
            logger.warn("Transaction " + getTransactionID() + " cannot process generated verisign message");
        }
    }

    public synchronized void exception(String message) throws TransactionException {
        getData().setStateMessage(message);
        getData().setIdentityName("SYSTEM");
        processDAO.signal(pi, EXCEPTION);
    }

    public synchronized void transit(RZMUser user, String transitionName) throws TransactionException {
        getData().setIdentityName(user.getLoginName());
        processDAO.signal(pi, transitionName);
    }

    public synchronized void transitTo(RZMUser user, String stateName) throws TransactionException {
        // hm? Token token = pi.getRootToken();
        Node destinationNode = pi.getProcessDefinition().getNode(stateName);
        if (destinationNode == null || !TransactionState.Name.nameStrings.contains(stateName))
            throw new TransactionException("no such state: " + stateName);
        getData().setIdentityName(user.getLoginName());
        processDAO.signal(pi, "TRANSITION_" + stateName);
    }

    public Set<SystemRole.SystemType> getReceivedContactConfirmations() {
        Set<SystemRole.SystemType> ret = new HashSet<SystemRole.SystemType>();
        if (getContactConfirmations() != null) {
            ret.addAll(getContactConfirmations().getContactsThatAccepted());
        }
        return ret;
    }

    public Set<ContactIdentity> getIdentitiesThatAccepted(TransactionState.Name stateName) {
        Set<ContactIdentity> ret = new HashSet<ContactIdentity>();
        ContactConfirmations confirmations = getContactConfirmations(stateName);
        if (confirmations != null) {
            ret.addAll(confirmations.getIdentitiesThatAccepted());
        }
        return ret;
    }

    public Set<ContactIdentity> getIdentitiesSupposedToAccept(TransactionState.Name stateName) {
        Set<ContactIdentity> ret = new HashSet<ContactIdentity>();
        ContactConfirmations confirmations = getContactConfirmations(stateName);
        if (confirmations != null) {
            ret.addAll(confirmations.getIdentitiesSupposedToAccept());
        }
        return ret;
    }

    public ContactConfirmations getContactConfirmations() {
        TransactionState.Name stateName = getState().getName();
        return getContactConfirmations(stateName);
    }

    public ContactConfirmations getContactConfirmations(TransactionState.Name stateName) {
        return getData().getContactConfirmations(stateName);
    }

    public boolean isRedelegation() {
        return getData().isRedelegation();
    }

    public void setRedelegation(boolean redelegation) {
        getData().setRedelegation(redelegation);
    }

    public String getSubmitterEmail() {
        return getData().getSubmitterEmail();
    }

    public void setSubmitterEmail(String submitterEmail) {
        getData().setSubmitterEmail(submitterEmail);
    }

    public String getComment() {
        return getData().getComment();
    }

    public void setComment(String comment) {
        getData().setComment(comment);
    }

    public int getEPPRetries() {
        return getData().getEPPRetries();
    }

    public void setEPPRetries(int retries) {
        getData().setEPPRetries(retries);
    }

    public String getStateMessage() {
        return getData().getStateMessage();
    }

    public void setStateMessage(String stateMessage) {
        getData().setStateMessage(stateMessage);
    }

    public String getEppRequestId() {
        return getData().getEppRequestId();
    }

    public void setEppRequestId(String eppRequestId) {
        getData().setEppRequestId(eppRequestId);
    }

    public USDoCConfirmation getUSDoCConfirmation() {
        return getData().getUSDoCConfirmation();
    }

    public void confirmChangeByUSDoC(RZMUser identity, TransactionChangeType type, boolean accept) throws TransactionException {
        if (getState().getName() != TransactionState.Name.PENDING_USDOC_APPROVAL) {
            throw new IllegalTransactionStateException(getState());
        }
        USDoCConfirmation confirmation = getUSDoCConfirmation();
        if (confirmation == null) {
            throw new IllegalStateException("no usdoc confirmation data setup");
        }
        switch (type) {
            case NAMESERVER_CHANGE:
                confirmation.setNameserversChangeConfirmation(accept);
                break;
            case DATABASE_CHANGE:
                confirmation.setDatabaseChangeConfirmation(accept);
                break;
        }
        verifyMismatch(accept);
        if (confirmation.isReceived()) {
            if (confirmation.isAccepted()) {
                transit(identity, "admin-accept");
            } else {
                transit(identity, "admin-reject");
            }
        } else {
            logger.info("there still are outstanding confirmation present.");
        }
    }

    private void verifyMismatch(boolean usdocAccept) throws TransactionException {
        EPPChangeStatus status = getData().getEppStatus();
        if (status != null) {
            if (usdocAccept) {
                if (status == EPPChangeStatus.docRejected) exception("status mismatch: usdoc accepted but verisign signalled docRejected");
            } else {
                if (status != EPPChangeStatus.docRejected && status.getOrderNumber() >= EPPChangeStatus.docApproved.getOrderNumber()) exception("status mismatch: usdoc rejected but verisign signalled: " + status);
            }
        }
    }
    public boolean isNameServerChange() {
        return getData().isNameServerChange();
    }

    public boolean isAdminContactChange() {
        return getData().isAdminContactChange();
    }

    public boolean isTechContactChange() {
        return getData().isTechContactChange();
    }

    public boolean isSupportingChange() {
        return getData().isSupportingChange();
    }

    public Set<String> getAddedOrUpdatedNameServers() {
        return getData().getAddedOrUpdatedNameServers();
    }

    public boolean isDatabaseChange() {
        return getData().isDatabaseChange();
    }


    public void setUsdocNotes(String usdocNotes) {
        getData().setUsdocNotes(usdocNotes);
    }

    public String getUsdocNotes() {
        return getData().getUsdocNotes();
    }

    public boolean areEmailsEnabled() {
        return getCurrentDomain().isEnableEmails();
    }

    public void addNotification(PNotification notification) {
        getData().addNotification(notification);
    }

    public Set<PNotification> getNotifications() {
        return getData().getNotifications();
    }

    public void deleteAllNotifications() {
        getNotifications().clear();
    }

    public void deleteNotifications(Collection<String> types) {
        if (types != null) {
            Set<PNotification> notifications = getNotifications();
            Set<PNotification> toRemove = new HashSet<PNotification>();
            for (PNotification notification : notifications) {
                if (types.contains(notification.getType())) {
                    toRemove.add(notification);
                }
            }
            notifications.removeAll(toRemove);
        }
    }

    public void resetConfirmation() {
        getData().resetConfirmation();
    }


    public Set<Domain> getImpactedDomains() {
        return getData().getImpactedDomains();
    }

    public List<String> getImpactedDomainsNames() {
        List<String> names = new ArrayList<String>();
        if (getData().getImpactedDomains() != null) {
            for (Domain domain : getData().getImpactedDomains()) {
                names.add(domain.getName());
            }
        }
        return names;
    }

    public boolean isNSSharedGlueChange() {
        Set<Domain> impactedDomains = getImpactedDomains();
        return impactedDomains != null && !impactedDomains.isEmpty();
    }

    public void setContactConfirmations(ContactConfirmations conf) {
        if (conf == null) return;
        conf.setStateName(getState().getName());
        getData().setContactConfirmations(conf);
    }

    public EPPChangeStatus getEPPStatus() {
        return getData().getEppStatus();
    }

    public void complete() throws TransactionException {
        TransactionState.Name state = getState().getName();
        if (state == TransactionState.Name.PENDING_ZONE_INSERTION) {
            systemAccept();
            systemAccept();
        } else if (state == TransactionState.Name.PENDING_ZONE_PUBLICATION) {
            systemAccept();
        } else if (state == TransactionState.Name.REJECTED) {
            exception("status mismatch: usdoc rejected but verisign signalled completed");
        } else {
            logger.warn("Transaction " + getTransactionID() + " cannot process generated verisign message");
        }
    }

    public boolean updateEPPStatus(EPPChangeStatus status) {
        CheckTool.checkNull(status, "status");
        EPPChangeStatus current = getEPPStatus();
        boolean updated = current == null || current != status;
        if (updated) {
            getData().setEppStatus(status);
        }
        return updated;
    }

    public void setTechnicalErrors(String errors) {
        getData().setTechnicalErrors(errors);
    }

    public String getTechnicalErrors() {
        return getData().getTechnicalErrors();
    }

    public boolean requiresSpecialReview() {
        return getData().requiresSpecialReview();
    }
}
