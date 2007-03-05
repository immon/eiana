package org.iana.rzm.trans;

import org.iana.rzm.common.TrackData;
import org.iana.rzm.common.TrackedObject;
import org.iana.rzm.domain.Domain;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.exe.TaskInstance;

import java.sql.Timestamp;
import java.util.List;
import java.util.Collection;

/**
 * This class represents a domain modification transaction.
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class Transaction implements TrackedObject {
    private static final String TRANSACTION_DATA = "TRANSACTION_DATA";
    private static final String TRACK_DATA = "TRACK_DATA";

    private ProcessInstance pi;

    public Transaction(String name) {
        pi = JbpmContextFactory.getJbpmContext().newProcessInstance(name);
        pi.getContextInstance().setVariable(TRANSACTION_DATA, new TransactionData());
        pi.getContextInstance().setVariable(TRACK_DATA, new TrackData());
        //JbpmContextFactory.getJbpmContext().save(pi);
    }

    private TransactionData getTransactionData() {
        return (TransactionData) pi.getContextInstance().getVariable(TRANSACTION_DATA);
    }

    private TrackData getTrackData() {
        return (TrackData) pi.getContextInstance().getVariable(TRACK_DATA);
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

    public List<TransactionAction> getActions() {
        return getTransactionData().getActions();
    }

    public void setActions(List<TransactionAction> actions) {
        getTransactionData().setActions(actions);
    }

    public TransactionState getState() {
        Token token = pi.getRootToken();
        Node node = token.getNode();
        TransactionState ts = new TransactionState();
        ts.setName(node.getName());
        ts.setStart(token.getStart());
        ts.setEnd(token.getEnd());
        for(Object o : node.getLeavingTransitions()) {
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

    public Long getId() {
        return getTrackData().getId();
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

    public synchronized void accept() {
        TaskInstance ti = getTaskInstance();
        Node n = ti.getToken().getNode();
        if (n.getDefaultLeavingTransition() != null)
            ti.end();
        else
            ti.end(StateTransition.ACCEPT);
    }

    public synchronized void reject() {
        TaskInstance ti = getTaskInstance();
        Node n = ti.getToken().getNode();
        ti.end(StateTransition.REJECT);
    }

    private TaskInstance getTaskInstance() {
        Collection<TaskInstance> tic = pi.getTaskMgmtInstance().getTaskInstances();
        if (tic == null && tic.size() == 0) throw new IllegalStateException("no task instances found for " + getTransactionID());
        return tic.iterator().next();
    }
}
