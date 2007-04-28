package org.iana.rzm.trans.jbpm.handlers;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.trans.confirmation.StateConfirmations;
import org.iana.rzm.user.AdminRole;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.configuration.ConfigurationException;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class RoleCalculator implements ActionHandler {
    private static final String ROLE_IANA = "IANA";
    private static final String ROLE_DOC = "USDoC";

    String transition;
    List<String> roles;

    private AdminRole.AdminType getType(String role) {
        if (ROLE_IANA.equals(role)) return AdminRole.AdminType.IANA;
        if (ROLE_DOC.equals(role)) return AdminRole.AdminType.GOV_OVERSIGHT;
        throw new IllegalArgumentException("role");
    }

    public void execute(ExecutionContext executionContext) throws Exception {
        if (roles == null || roles.isEmpty())
            throw new ConfigurationException("no roles specified");

        TransactionData td = (TransactionData) executionContext.getContextInstance().getVariable("TRANSACTION_DATA");
        Token token = executionContext.getProcessInstance().getRootToken();
        Node node = token.getNode();

        if (transition == null || transition.length() == 0) {
            for (Object o : node.getLeavingTransitions()) {
                Transition t = (Transition) o;
                for (String role : roles)
                    td.addTransitionConfirmation(node.getName(), t.getName(),
                            new RoleConfirmation(new AdminRole(getType(role))));
            }
        } else {
            Transition t = (Transition) node.getLeavingTransitionsMap().get(transition);
            if (t == null)
                throw new ConfigurationException("nonexistent transition: " + transition);
            for (String role : roles)
                td.addTransitionConfirmation(node.getName(), t.getName(),
                        new RoleConfirmation(new AdminRole(getType(role))));
        }
    }
}
