package org.iana.rzm.trans;

import org.iana.objectdiff.ObjectChange;
import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.confirmation.*;
import org.iana.rzm.auth.Identity;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import java.sql.Timestamp;
import java.util.List;

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

    private void addStateLogEntry(TransactionState state, String userName) {
        getTransactionData().addStateLogEntry(new TransactionStateLogEntry(state, userName));
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

    private void signal(String transitionName, Identity user) {
        TransactionState prevState = getState();
        pi.signal(transitionName);
        TransactionState state = getState();
        prevState.setEnd(state.getStart());
        addStateLogEntry(prevState, user.getName());
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
            signal(StateTransition.ACCEPT, user);
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
            signal(StateTransition.REJECT, user);
        } else
            throw new UserConfirmationNotExpected();
    }

    public synchronized void transit(Identity user, String transitionName) throws TransactionException {
        if (transitionName.equals(StateTransition.ACCEPT)) accept(user);
        Token token = pi.getRootToken();
        Node node = token.getNode();
        TransitionConfirmations tc = getTransactionData().getTransitionConfirmations(node.getName());
        if (tc == null) throw new UserNotAuthorizedToTransit();
        if (tc.isAcceptableBy(transitionName, user)) {
            signal(transitionName, user);
        } else
            throw new UserNotAuthorizedToTransit();
    }

    public synchronized void transitTo(String stateName) throws TransactionException {
        Token token = pi.getRootToken();
        Node destinationNode = pi.getProcessDefinition().getNode(stateName);
        if (destinationNode == null) throw new TransactionException("no such state: " + stateName);
        token.setNode(destinationNode);
    }
}
