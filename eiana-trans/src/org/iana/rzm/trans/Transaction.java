package org.iana.rzm.trans;

import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.common.validators.CheckTool;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.change.ObjectChange;
import org.iana.rzm.trans.confirmation.AlreadyAcceptedByUser;
import org.iana.rzm.trans.confirmation.Confirmation;
import org.iana.rzm.trans.confirmation.NotAcceptableByUser;
import org.iana.rzm.trans.confirmation.TransitionConfirmations;
import org.iana.rzm.user.RZMUser;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import java.sql.Timestamp;

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

    private TransactionData getTransactionData() {
        return (TransactionData) pi.getContextInstance().getVariable(TRANSACTION_DATA);
    }

    private TrackData getTrackData() {
        return ((TransactionData) pi.getContextInstance().getVariable(TRANSACTION_DATA)).getTrackData();
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
        Token token = pi.getRootToken();
        Node node = token.getNode();
        TransactionState ts = new TransactionState();
        ts.setName(node.getName());
        ts.setStart(token.getStart());
        if (token.getEnd() != null)
            ts.setEnd(token.getEnd());
        for (Object o : node.getLeavingTransitions()) {
            Transition transition = (Transition) o;
            ts.addAvailableTransition(new StateTransition(transition.getName()));
        }
        return ts;
    }

    public Timestamp getStart() {
        return new Timestamp(pi.getStart().getTime());
    }

    public void setStart(Timestamp start) {
        pi.setStart(start);
    }

    public Timestamp getEnd() {
        return new Timestamp(pi.getEnd().getTime());
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

    public synchronized void accept(RZMUser user) throws TransactionException {
        try {
            Token token = pi.getRootToken();
            Node node = token.getNode();
            Confirmation confirmation = getTransactionData().getStateConfirmations(node.getName());
            if (confirmation != null) {
                if (!confirmation.accept(user))
                    return;
            }
            pi.signal(StateTransition.ACCEPT);
        } catch (AlreadyAcceptedByUser e) {
            throw new UserAlreadyAccepted(e);
        } catch (NotAcceptableByUser e) {
            throw new UserConfirmationNotExpected(e);
        }
    }

    public synchronized void reject(RZMUser user) throws TransactionException {
        transit(user, StateTransition.REJECT);
    }

    public synchronized void transit(RZMUser user, String transitionName) throws TransactionException {
        if (transitionName.equals(StateTransition.ACCEPT)) accept(user);
        Token token = pi.getRootToken();
        Node node = token.getNode();
        TransitionConfirmations tc = getTransactionData().getTransitionConfirmations(node.getName());
        if (tc == null) throw new UserNotAuthorizedToTransit();
        if (tc.isAcceptableBy(transitionName, user))
            pi.signal(transitionName);
        else
            throw new UserNotAuthorizedToTransit();
    }
}
