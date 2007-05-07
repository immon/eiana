package org.iana.rzm.trans.jbpm.handlers;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.user.AdminRole;
import org.jbpm.configuration.ConfigurationException;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import java.util.List;

/**
 * @author Patrycja Wegrzynowicz
 */
public class RoleCalculator implements ActionHandler {
    private static final String ROLE_IANA = "IANA";
    private static final String ROLE_DOC = "USDoC";

    List<String> transitions;
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

        if (transitions == null || transitions.isEmpty()) {
            for (Object o : node.getLeavingTransitions()) {
                Transition t = (Transition) o;
                for (String role : roles)
                    td.addTransitionConfirmation(node.getName(), t.getName(),
                            new RoleConfirmation(new AdminRole(getType(role))));
            }
        } else {
            for (String transitionName : transitions) {
                Transition transition = (Transition) node.getLeavingTransitionsMap().get(transitionName);
                if (transition == null)
                    throw new ConfigurationException("nonexistent transition: " + transitionName);
                for (String role : roles)
                    td.addTransitionConfirmation(node.getName(), transition.getName(),
                            new RoleConfirmation(new AdminRole(getType(role))));
            }
        }
    }
}
