package org.iana.rzm.trans.confirmation.contact;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.confirmation.StateConfirmations;
import org.iana.rzm.trans.confirmation.MandatoryRoleConfirmations;
import org.iana.rzm.trans.confirmation.RoleConfirmation;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.domain.Contact;

import java.util.List;
import java.util.Set;
import java.util.HashSet;
import java.rmi.server.UID;

import pl.nask.util.StringTool;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ContactConfirmationCalculator implements ActionHandler {

    public void execute(ExecutionContext executionContext) throws Exception {
        Transaction trans = new Transaction(executionContext.getProcessInstance());
        Set<ContactIdentity> contacts = new HashSet<ContactIdentity>();
        for (Contact tc : trans.getCurrentDomain().getTechContacts()) {
            contacts.add(new ContactIdentity(SystemRole.SystemType.TC, tc, generateToken()));
        }
        for (Contact ac : trans.getCurrentDomain().getAdminContacts()) {
            contacts.add(new ContactIdentity(SystemRole.SystemType.AC, ac, generateToken()));
        }
        trans.getTransactionData().setContactConfirmations(new ContactConfirmations(contacts));
    }

    public String generateToken() {
        return StringTool.encodeMd5(new UID().toString());
    }
}
