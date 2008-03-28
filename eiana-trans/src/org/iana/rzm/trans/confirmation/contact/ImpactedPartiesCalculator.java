package org.iana.rzm.trans.confirmation.contact;

import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.HashSet;
import java.util.Set;

/**
 * @author Patrycja Wegrzynowicz
 */
public class ImpactedPartiesCalculator extends ContactConfirmationCalculator {

    public void doExecute(ExecutionContext executionContext) throws Exception {
        Transaction trans = getTransaction(executionContext);
        Set<ContactIdentity> contacts = new HashSet<ContactIdentity>();
        for (Domain d : trans.getImpactedDomains()) {
            if (d.getTechContact() != null) {
                contacts.add(new ContactIdentity(SystemRole.SystemType.TC, d.getTechContact(), generateToken(), false, true, d.getName()));
            }
            if (d.getAdminContact() != null) {
                contacts.add(new ContactIdentity(SystemRole.SystemType.AC, d.getAdminContact(), generateToken(), false, true, d.getName()));
            }
        }
        System.out.println("====== impacted parties calculator");
        trans.setContactConfirmations(new ContactConfirmations(contacts));
    }
}
