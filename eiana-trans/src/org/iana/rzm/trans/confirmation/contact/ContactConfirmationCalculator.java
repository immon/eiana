package org.iana.rzm.trans.confirmation.contact;

import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.SimpleChange;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;
import pl.nask.util.StringTool;

import java.rmi.server.UID;
import java.util.HashSet;
import java.util.Set;

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
        ObjectChange change = trans.getTransactionData().getDomainChange();
        if (change != null && change.getFieldChanges() != null) {
            if (change.getFieldChanges().containsKey("techContact")) {
                ObjectChange contactChange = (ObjectChange) change.getFieldChanges().get("techContact");
                if (contactChange.getFieldChanges().containsKey("email")) {
                    SimpleChange emailChange = (SimpleChange) contactChange.getFieldChanges().get("email");
                    String proposedEmail = emailChange.getNewValue();
                    contacts.add(new ContactIdentity(SystemRole.SystemType.TC, contactChange.getId(), proposedEmail, generateToken()));
                }
            }
            if (change.getFieldChanges().containsKey("adminContact")) {
                ObjectChange contactChange = (ObjectChange) change.getFieldChanges().get("adminContact");
                if (contactChange.getFieldChanges().containsKey("email")) {
                    SimpleChange emailChange = (SimpleChange) contactChange.getFieldChanges().get("email");
                    String proposedEmail = emailChange.getNewValue();
                    contacts.add(new ContactIdentity(SystemRole.SystemType.AC, contactChange.getId(), proposedEmail, generateToken()));
                }
            }
        }
        trans.getTransactionData().setContactConfirmations(new ContactConfirmations(contacts));
    }

    public String generateToken() {
        return StringTool.encodeMd5(new UID().toString());
    }
}
