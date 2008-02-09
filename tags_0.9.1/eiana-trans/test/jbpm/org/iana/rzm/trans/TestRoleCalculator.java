package org.iana.rzm.trans;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.StateConfirmations;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.user.AdminRole;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

/**
 * @author Jakub Laszkiewicz
 */
public class TestRoleCalculator implements ActionHandler {
    private static final String ROLE_IANA = "IANA";
    private static final String ROLE_DOC = "USDoC";

    String role;

    private AdminRole.AdminType getType(String role) {
        if (role == null || role.length() == 0)
            throw new IllegalArgumentException("no role specified");
        if (ROLE_IANA.equals(role)) return AdminRole.AdminType.IANA;
        if (ROLE_DOC.equals(role)) return AdminRole.AdminType.GOV_OVERSIGHT;
        throw new IllegalArgumentException("role not expected: " + role);
    }

    public void execute(ExecutionContext executionContext) throws Exception {
        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");

        StateConfirmations sc = new StateConfirmations();
        sc.addConfirmation(new RoleConfirmation(new AdminRole(getType(role))));

        Token token = executionContext.getProcessInstance().getRootToken();
        Node node = token.getNode();
        td.setStateConfirmations(node.getName(), sc);
    }
}
