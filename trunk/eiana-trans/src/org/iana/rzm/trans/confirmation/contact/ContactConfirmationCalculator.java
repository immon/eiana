package org.iana.rzm.trans.confirmation.contact;

import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.SimpleChange;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.domain.HostManager;
import org.iana.rzm.domain.Host;
import org.iana.rzm.domain.DomainManager;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.HashSet;
import java.util.Set;
import java.util.Random;
import java.util.List;

/**
 * @author Jakub Laszkiewicz
 * @author Patrycja Wegrzynowicz
 */
public class ContactConfirmationCalculator implements ActionHandler {

    public void execute(ExecutionContext executionContext) throws Exception {
        Transaction trans = new Transaction(executionContext.getProcessInstance());
        Set<ContactIdentity> contacts = new HashSet<ContactIdentity>();
        Domain domain = trans.getCurrentDomain();
        if (domain.getTechContact() != null) {
            contacts.add(new ContactIdentity(SystemRole.SystemType.TC, domain.getTechContact(), generateToken(), false));
        }
        if (domain.getAdminContact() != null) {
            contacts.add(new ContactIdentity(SystemRole.SystemType.AC, domain.getAdminContact(), generateToken(), false));
        }
        ObjectChange change = trans.getTransactionData().getDomainChange();
        if (change != null && change.getFieldChanges() != null) {
            if (change.getFieldChanges().containsKey("techContact")) {
                ObjectChange contactChange = (ObjectChange) change.getFieldChanges().get("techContact");
                if (contactChange.getFieldChanges().containsKey("email")) {
                    SimpleChange emailChange = (SimpleChange) contactChange.getFieldChanges().get("email");
                    String proposedEmail = emailChange.getNewValue();
                    String name;
                    if (contactChange.getFieldChanges().containsKey("name")) {
                        SimpleChange nameChange = (SimpleChange) contactChange.getFieldChanges().get("name");
                        name = nameChange.getNewValue();
                    } else {
                        name = contactChange.getId();
                    }
                    contacts.add(new ContactIdentity(SystemRole.SystemType.TC, name, proposedEmail, generateToken(), true));
                }
            }
            if (change.getFieldChanges().containsKey("adminContact")) {
                ObjectChange contactChange = (ObjectChange) change.getFieldChanges().get("adminContact");
                if (contactChange.getFieldChanges().containsKey("email")) {
                    SimpleChange emailChange = (SimpleChange) contactChange.getFieldChanges().get("email");
                    String proposedEmail = emailChange.getNewValue();
                    String name;
                    if (contactChange.getFieldChanges().containsKey("name")) {
                        SimpleChange nameChange = (SimpleChange) contactChange.getFieldChanges().get("name");
                        name = nameChange.getNewValue();
                    } else {
                        name = contactChange.getId();
                    }
                    contacts.add(new ContactIdentity(SystemRole.SystemType.AC, name, proposedEmail, generateToken(), true));
                }
            }
            if (trans.isNameServerChange()) {
                // check if shared...
                HostManager hostManager = (HostManager) executionContext.getJbpmContext().getObjectFactory().createObject("hostManager");
                DomainManager domainManager = (DomainManager) executionContext.getJbpmContext().getObjectFactory().createObject("domainManager");
                Set<String> updatedNameServers = trans.getAddedOrUpdatedNameServers();
                List<Domain> domains = domainManager.findDelegatedTo(updatedNameServers);
                Set<String> processed = new HashSet<String>();
                for (Domain d : domains) {
                    if (!processed.contains(d.getName())) {
                        processed.add(d.getName());
                        if (d.getTechContact() != null) {
                            contacts.add(new ContactIdentity(SystemRole.SystemType.TC, d.getTechContact(), generateToken(), false));
                        }
                        if (d.getAdminContact() != null) {
                            contacts.add(new ContactIdentity(SystemRole.SystemType.AC, d.getAdminContact(), generateToken(), false));
                        }
                    }
                }
            }
        }
        trans.getTransactionData().setContactConfirmations(new ContactConfirmations(contacts));
    }

    public String generateToken() {
        String stime = Long.toString(System.currentTimeMillis(), Character.MAX_RADIX);
        if (stime.length() > 6) stime = stime.substring(stime.length()-6);
        int max = (int) Math.pow(Character.MAX_RADIX, 8-stime.length());
        int pre = random.nextInt(max);
        String rand = Integer.toString(pre, Character.MAX_RADIX);
        String ret = rand+stime;
        while (ret.length() < 8) ret = "0"+ret;
        return ret;
    }
    
    static Random random = new Random(System.currentTimeMillis());
}
