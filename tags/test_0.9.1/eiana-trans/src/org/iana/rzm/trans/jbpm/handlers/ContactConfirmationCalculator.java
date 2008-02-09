package org.iana.rzm.trans.jbpm.handlers;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.MandatoryRoleConfirmations;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.trans.confirmation.StateConfirmations;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

/**
 * This class calculates parties that need to consent upon a process proceeds to the next state
 * from the PENDING_CONTACT_CONFIRMATION state.
 * <p/>
 * NB Calculated parties are added to the existing set, when already initialized by another handler(s).
 *
 * @author Patrycja Wegrzynowicz
 * @author Jakub Laszkiewicz
 */
public class ContactConfirmationCalculator extends ActionExceptionHandler {

    public void doExecute(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        Token token = executionContext.getProcessInstance().getRootToken();
        Node node = token.getNode();
        StateConfirmations sc = (StateConfirmations) td.getStateConfirmations(node.getName());
        if (sc == null) sc = new StateConfirmations();

        MandatoryRoleConfirmations mrcAc = new MandatoryRoleConfirmations(td.getCurrentDomain().getName(), SystemRole.SystemType.AC);
        if (!mrcAc.isReceived()) sc.addConfirmation(mrcAc);
        MandatoryRoleConfirmations mrcTc = new MandatoryRoleConfirmations(td.getCurrentDomain().getName(), SystemRole.SystemType.TC);
        if (!mrcTc.isReceived()) sc.addConfirmation(mrcTc);
        sc.addConfirmation(new RoleConfirmation(new SystemRole(SystemRole.SystemType.AC, td.getCurrentDomain().getName(), true, false)));
        sc.addConfirmation(new RoleConfirmation(new SystemRole(SystemRole.SystemType.TC, td.getCurrentDomain().getName(), true, false)));

        td.setStateConfirmations(node.getName(), sc);
    }
}
