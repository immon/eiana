package org.iana.rzm.trans;

import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.auth.Identity;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.confirmation.AlreadyAcceptedByUser;
import org.iana.rzm.trans.confirmation.Confirmation;
import org.iana.rzm.trans.confirmation.NotAcceptableByUser;
import org.iana.rzm.trans.confirmation.TransitionConfirmations;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.user.RZMUser;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import java.sql.Timestamp;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

/**
 * This class represents a domain modification transaction.
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class Transaction implements TrackedObject {

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
        if (tc == null) throw new UserNotAuthorizedToTransit();
        if (tc.isAcceptableBy(transitionName, user)) {
            if (user instanceof RZMUser)
                getTransactionData().setIdentityName(((RZMUser) user).getLoginName());
            pi.signal(transitionName);
        } else
            throw new UserNotAuthorizedToTransit();
    }

    public synchronized void transitTo(Identity user, String stateName) throws TransactionException {
        Token token = pi.getRootToken();
        Node destinationNode = pi.getProcessDefinition().getNode(stateName);
        if (destinationNode == null) throw new TransactionException("no such state: " + stateName);
        if (user instanceof RZMUser)
            getTransactionData().setIdentityName(((RZMUser) user).getLoginName());
        token.setNode(destinationNode);
    }

    public Set<SystemRole.SystemType> getReceivedContactConfirmations() {
        Set<SystemRole.SystemType> ret = new HashSet<SystemRole.SystemType>();
        if (getTransactionData().getContactConfirmations() != null) {
            ret.addAll(getTransactionData().getContactConfirmations().getContactsThatAccepted());
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
}
