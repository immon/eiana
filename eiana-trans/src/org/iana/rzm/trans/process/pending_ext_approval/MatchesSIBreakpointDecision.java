package org.iana.rzm.trans.process.pending_ext_approval;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.process.general.ctx.TransactionContext;
import org.iana.rzm.trans.process.general.handlers.DecisionExceptionHandler;
import org.jbpm.graph.exe.ExecutionContext;

/**
 * @author: Piotr Tkaczyk
 */
public class MatchesSIBreakpointDecision extends DecisionExceptionHandler {

    public String doDecide(ExecutionContext executionContext) throws Exception {
        TransactionContext ctx = new TransactionContext(executionContext);
        Transaction trans = ctx.getTransaction();
        Domain domain = trans.getCurrentDomain();
        if (domain.hasBreakpoint(Domain.Breakpoint.ANY_CHANGE_EXT_REVIEW)) {
            return "yes";
        }
        if (trans.isNameServerChange() && domain.hasBreakpoint(Domain.Breakpoint.NS_CHANGE_EXT_REVIEW)) {
            return "yes";
        }
        if (trans.isSupportingChange() && domain.hasBreakpoint(Domain.Breakpoint.SO_CHANGE_EXT_REVIEW)) {
            return "yes";
        }
        if (trans.isAdminContactChange() && domain.hasBreakpoint(Domain.Breakpoint.AC_CHANGE_EXT_REVIEW)) {
            return "yes";
        }
        if (trans.isTechContactChange() && domain.hasBreakpoint(Domain.Breakpoint.TC_CHANGE_EXT_REVIEW)) {
            return "yes";
        }
        return "no";
    }
}
