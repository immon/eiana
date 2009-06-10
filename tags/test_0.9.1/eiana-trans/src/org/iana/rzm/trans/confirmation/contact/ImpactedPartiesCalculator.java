package org.iana.rzm.trans.confirmation.contact;

import org.iana.rzm.trans.TransactionData;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.jbpm.handlers.*;
import org.iana.rzm.trans.confirmation.StateConfirmations;
import org.iana.rzm.trans.confirmation.contact.ContactIdentity;
import org.iana.rzm.trans.confirmation.contact.ContactConfirmations;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.user.SystemRole;
import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.SimpleChange;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;

import java.util.Set;
import java.util.List;
import java.util.HashSet;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartiesCalculator extends ContactConfirmationCalculator {

    public void doExecute(ExecutionContext executionContext) throws Exception {
        Transaction trans = new Transaction(executionContext.getProcessInstance());
        Set<ContactIdentity> contacts = new HashSet<ContactIdentity>();
        if (trans.isNameServerChange()) {
            // check if shared...
            DomainManager domainManager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");
            Set<String> updatedNameServers = trans.getAddedOrUpdatedNameServers();
            if (!updatedNameServers.isEmpty()) {
                List<Domain> domains = domainManager.findDelegatedTo(updatedNameServers);
                Set<String> processed = new HashSet<String>();
                for (Domain d : domains) {
                    if (!processed.contains(d.getName())) {
                        processed.add(d.getName());
                        if (d.getTechContact() != null) {
                            contacts.add(new ContactIdentity(SystemRole.SystemType.TC, d.getTechContact(), generateToken(), false, true, d.getName()));
                        }
                        if (d.getAdminContact() != null) {
                            contacts.add(new ContactIdentity(SystemRole.SystemType.AC, d.getAdminContact(), generateToken(), false, true, d.getName()));
                        }
                    }
                }
            }
        }
        trans.getTransactionData().setContactConfirmations(new ContactConfirmations(contacts));
    }
}