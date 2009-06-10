package org.iana.rzm.trans.confirmation.contact;

import org.iana.objectdiff.ObjectChange;
import org.iana.objectdiff.SimpleChange;
import org.iana.rzm.domain.Domain;
import org.iana.rzm.trans.Transaction;
import org.iana.rzm.trans.process.general.handlers.ActionExceptionHandler;
import org.iana.rzm.user.SystemRole;
import org.jbpm.graph.exe.ExecutionContext;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.security.SecureRandom;
import java.security.NoSuchAlgorithmException;

/**
 * @author Jakub Laszkiewicz
 * @author Patrycja Wegrzynowicz
 */
public class ContactConfirmationCalculator extends ActionExceptionHandler {

    public void doExecute(ExecutionContext executionContext) throws Exception {
        Transaction trans = getTransaction(executionContext);
        Set<ContactIdentity> contacts = new HashSet<ContactIdentity>();
        Domain domain = trans.getCurrentDomain();
        if (domain.getTechContact() != null) {
            contacts.add(new ContactIdentity(SystemRole.SystemType.TC, domain.getTechContact(), generateToken(), false, domain.getName()));
        }
        if (domain.getAdminContact() != null) {
            contacts.add(new ContactIdentity(SystemRole.SystemType.AC, domain.getAdminContact(), generateToken(), false, domain.getName()));
        }
        ObjectChange change = trans.getData().getDomainChange();
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
                    contacts.add(new ContactIdentity(SystemRole.SystemType.TC, name, proposedEmail, generateToken(), true, false, domain.getName()));
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
                    contacts.add(new ContactIdentity(SystemRole.SystemType.AC, name, proposedEmail, generateToken(), true, false, domain.getName()));
                }
            }
        }
        trans.setContactConfirmations(new ContactConfirmations(contacts));
    }

    public static String generateToken() throws NoSuchAlgorithmException {
        Random random = SecureRandom.getInstance("SHA1PRNG");
        String sTime = Long.toString(random.nextInt(), Character.MAX_RADIX);
        if (sTime.length() > 6) sTime = sTime.substring(sTime.length()-6);
        int max = (int) Math.pow(Character.MAX_RADIX, 8-sTime.length());
        int pre = random.nextInt(max);
        String rand = Integer.toString(pre, Character.MAX_RADIX);
        String ret = rand+sTime;
        while (ret.length() < 8) ret = "0"+ret;
        return ret;
    }

}
