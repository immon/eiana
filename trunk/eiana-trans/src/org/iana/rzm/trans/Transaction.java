package org.iana.rzm.trans;

import org.apache.log4j.Logger;
import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.auth.Identity;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.change.TransactionChangeType;
import org.iana.rzm.trans.confirmation.AlreadyAcceptedByUser;
import org.iana.rzm.trans.confirmation.Confirmation;
import org.iana.rzm.trans.confirmation.NotAcceptableByUser;
import org.iana.rzm.trans.confirmation.TransitionConfirmations;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.confirmation.usdoc.USDoCConfirmation;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * This class represents a domain modification transaction.
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class Transaction implements TrackedObject {

    private static final Logger logger = Logger.getLogger(Transaction.class);
    
    private static final String TRANSACTION_DATA = "TRANSACTION_DATA";
    private ProcessInstance pi;

    public Transaction(ProcessInstance pi) {
        CheckTool.checkNull(pi, "process instance");
        this.pi = pi;
        if (!this.pi.getContextInstance().hasVariable(TRANSACTION_DATA))
            this.pi.getContextInstance().setVariable(TRANSACTION_DATA, new TransactionData());
    }

    public TransactionData getTransactionData() {
        return (TransactionData) pi.getContextInstance().getVariable(TRANSACTION_DATA);
    }

    private TrackData getTrackData() {
        return ((TransactionData) pi.getContextInstance().getVariable(TRANSACTION_DATA)).getTrackData();
    }

    public Long getObjId() {
        return getTransactionID();
    }

    public Long getTransactionID() {
        return pi.getId();
    }

    public Long getTicketID() {
        return getTransactionData().getTicketID();
    }

    public void setTicketID(Long ticketID) {
        getTransactionData().setTicketID(ticketID);
    }

    public String getName() {
        return pi.getProcessDefinition().getName();
    }

    public Domain getCurrentDomain() {
        return getTransactionData().getCurrentDomain();
    }

    public void setCurrentDomain(Domain currentDomain) {
        getTransactionData().setCurrentDomain(currentDomain);
    }

    public ObjectChange getDomainChange() {
        return getTransactionData().getDomainChange();
    }

    public void setDomainChange(ObjectChange change) {
        getTransactionData().setDomainChange(change);
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
        return getTransactionData().getStateLog();
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
        pi.signal(StateTransition.ACCEPT);
    }

    public synchronized void accept(Identity user) throws TransactionException {
        try {
            Token token = pi.getRootToken();
            Node node = token.getNode();
            String state = node.getName();

            Confirmation confirmation = "PENDING_CONTACT_CONFIRMATION".equals(state) ?
                    getTransactionData().getContactConfirmations() :
                    getTransactionData().getStateConfirmations(node.getName());

            if (confirmation == null) throw new UserConfirmationNotExpected();
            if (!confirmation.accept(user))
                return;
            if (user instanceof RZMUser)
                getTransactionData().setIdentityName(((RZMUser) user).getLoginName());
            pi.signal(StateTransition.ACCEPT);
        } catch (AlreadyAcceptedByUser e) {
            throw new UserAlreadyAccepted(e);
        } catch (NotAcceptableByUser e) {
            throw new UserConfirmationNotExpected(e);
        }
    }

    public synchronized void reject(Identity user) throws TransactionException {
        Token token = pi.getRootToken();
        Node node = token.getNode();
        String state = node.getName();

        Confirmation confirmation = "PENDING_CONTACT_CONFIRMATION".equals(state) ?
                getTransactionData().getContactConfirmations() :
                getTransactionData().getStateConfirmations(node.getName());

        if (confirmation == null) throw new UserConfirmationNotExpected();
        if (confirmation.isAcceptableBy(user)) {
            if (user instanceof RZMUser)
                getTransactionData().setIdentityName(((RZMUser) user).getLoginName());
            pi.signal(StateTransition.REJECT);
        } else
            throw new UserConfirmationNotExpected();
    }

    public synchronized void transit(Identity user, String transitionName) throws TransactionException {
//        if (transitionName.equals(StateTransition.ACCEPT)) accept(user);
        Token token = pi.getRootToken();
        Node node = token.getNode();
        TransitionConfirmations tc = getTransactionData().getTransitionConfirmations(node.getName());
        if (tc == null) {
            String s =
                    "User " + user.getName() + "  " + user.getEmail() + " is not Authorized to move request to " + transitionName;
            throw new UserNotAuthorizedToTransit(s);
        }
        if (tc.isAcceptableBy(transitionName, user)) {
            if (user instanceof RZMUser) {
                getTransactionData().setIdentityName(((RZMUser) user).getLoginName());
            }
            pi.signal(transitionName);
        } else {
            throw new UserNotAuthorizedToTransit();
        }
    }

    public synchronized void transitTo(Identity user, String stateName) throws TransactionException {
        Token token = pi.getRootToken();
        Node destinationNode = pi.getProcessDefinition().getNode(stateName);
        if (destinationNode == null || !TransactionState.Name.nameStrings.contains(stateName))
            throw new TransactionException("no such state: " + stateName);
        if (user instanceof RZMUser)
            getTransactionData().setIdentityName(((RZMUser) user).getLoginName());
        token.signal("TRANSITION_" + stateName);
    }

    public Set<SystemRole.SystemType> getReceivedContactConfirmations() {
        Set<SystemRole.SystemType> ret = new HashSet<SystemRole.SystemType>();
        if (getTransactionData().getContactConfirmations() != null) {
            ret.addAll(getTransactionData().getContactConfirmations().getContactsThatAccepted());
        }
        return ret;
    }

    public Set<ContactIdentity> getIdentitiesThatAccepted() {
        Set<ContactIdentity> ret = new HashSet<ContactIdentity>();
        if (getTransactionData().getContactConfirmations() != null) {
            ret.addAll(getTransactionData().getContactConfirmations().getIdentitiesThatAccepted());
        }
        return ret;
    }

    public Set<ContactIdentity> getIdentitiesSupposedToAccept() {
        Set<ContactIdentity> ret = new HashSet<ContactIdentity>();
        if (getTransactionData().getContactConfirmations() != null) {
            ret.addAll(getTransactionData().getContactConfirmations().getIdentitiesSupposedToAccept());
        }
        return ret;
    }

    public boolean isRedelegation() {
        return getTransactionData().isRedelegation();
    }

    public void setRedelegation(boolean redelegation) {
        getTransactionData().setRedelegation(redelegation);
    }

    public String getSubmitterEmail() {
        return getTransactionData().getSubmitterEmail();
    }

    public void setSubmitterEmail(String submitterEmail) {
        getTransactionData().setSubmitterEmail(submitterEmail);
    }

    public String getComment() {
        return getTransactionData().getComment();
    }

    public void setComment(String comment) {
        getTransactionData().setComment(comment);
    }

    public int getEPPRetries() {
        return getTransactionData().getEPPRetries();
    }

    public void setEPPRetries(int retries) {
        getTransactionData().setEPPRetries(retries);
    }

    public String getStateMessage() {
        return getTransactionData().getStateMessage();
    }

    public void setStateMessage(String stateMessage) {
        getTransactionData().setStateMessage(stateMessage);
    }

    public String getEppRequestId() {
        return getTransactionData().getEppRequestId();
    }

    public void setEppRequestId(String eppRequestId) {
        getTransactionData().setEppRequestId(eppRequestId);
    }

    public USDoCConfirmation getUSDoCConfirmation() {
        return getTransactionData().getUSDoCConfirmation();
    }

    public void confirmChangeByUSDoC(Identity identity, TransactionChangeType type, boolean accept) throws TransactionException {
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

    public boolean isNameServerChange() {
        return getTransactionData().isNameServerChange();
    }

    public boolean isDatabaseChange() {
        return getTransactionData().isDatabaseChange();
    }


    public void setUsdocNotes(String usdocNotes) {
        getTransactionData().setUsdocNotes(usdocNotes);
    }

    public String getUsdocNotes() {
        return getTransactionData().getUsdocNotes();
    }
}
