package org.iana.rzm.trans.confirmation.contact;

import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.user.SystemRole;
import org.iana.rzm.domain.Contact;
import org.iana.rzm.domain.Domain;

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
        Domain domain = trans.getCurrentDomain();
        if (domain.getTechContact() != null) {
            contacts.add(new ContactIdentity(SystemRole.SystemType.TC, domain.getTechContact(), generateToken()));
        }
        if (domain.getAdminContact() != null) {
            contacts.add(new ContactIdentity(SystemRole.SystemType.AC, domain.getAdminContact(), generateToken()));
        }
        trans.getTransactionData().setContactConfirmations(new ContactConfirmations(contacts));
    }

    public String generateToken() {
        return StringTool.encodeMd5(new UID().toString());
    }
}
