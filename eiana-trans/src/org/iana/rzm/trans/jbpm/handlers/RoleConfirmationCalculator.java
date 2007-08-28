package org.iana.rzm.trans.jbpm.handlers;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.trans.confirmation.StateConfirmations;
import org.iana.rzm.user.AdminRole;
import org.jbpm.configuration.ConfigurationException;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import java.util.List;

/**
 * This class sets administrative roles, who need to consent upon a process proceeds to the next state.
 * <p/>
 * NB Specified roles are added to the existing set, when already initialized by another handler(s).
 *
 * @author Jakub Laszkiewicz
 */
public class RoleConfirmationCalculator extends ActionExceptionHandler {
    private static final String ROLE_IANA = "IANA";
    private static final String ROLE_DOC = "USDoC";

    List<String> roles;

    private AdminRole.AdminType getType(String role) {
        if (ROLE_IANA.equals(role)) return AdminRole.AdminType.IANA;
        if (ROLE_DOC.equals(role)) return AdminRole.AdminType.GOV_OVERSIGHT;
        throw new IllegalArgumentException("role");
    }

    public void doExecute(ExecutionContext executionContext) throws Exception {
        if (roles == null || roles.isEmpty())
            throw new ConfigurationException("no roles specified");

        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        Token token = executionContext.getProcessInstance().getRootToken();
        Node node = token.getNode();
        StateConfirmations sc = (StateConfirmations) td.getStateConfirmations(node.getName());

        if (sc == null) sc = new StateConfirmations();
        for (String role : roles)
            sc.addConfirmation(new RoleConfirmation(new AdminRole(getType(role))));

        td.setStateConfirmations(node.getName(), sc);
    }
}
